package com.frybits.acme.utils

import android.util.Log
import com.frybits.acme.models.Driver
import com.frybits.acme.models.Route
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val LOG_TAG = "AcmeAlgo"

interface AcmeAlgo {

    suspend fun generateDriverRouteMap(drivers: Collection<Driver>, routes: Collection<Route>): Map<Driver, Pair<Double, Route>>
}

/**
 * This algorithm takes a greedy approach for determining the best score for the driver/route to use.
 */
class AcmeAlgoImpl @Inject constructor(): AcmeAlgo {

    override suspend fun generateDriverRouteMap(drivers: Collection<Driver>, routes: Collection<Route>): Map<Driver, Pair<Double, Route>> {
        // Perform the heavy work in a background thread
        return withContext(Dispatchers.Default) {
            // Calculate and store
            val scoredDriverRoutes = routes.flatMap routes@ { route ->
                return@routes drivers.map drivers@ { driver ->
                    return@drivers Triple(calculateDriverRouteSustainabilityScore(driver, route), driver, route)
                }
            }.sortedByDescending { (ss) -> ss } // Sort by highest score for greedy approach

            val availableDrivers = drivers.toHashSet()
            val availableRoutes = routes.toHashSet()

            var total = 0.0
            val map = buildMap {
                scoredDriverRoutes.forEach { (score, driver, route) ->
                    if (availableDrivers.contains(driver) && availableRoutes.contains(route)) {
                        total += score
                        put(driver, score to route)
                        availableDrivers.remove(driver)
                        availableRoutes.remove(route)
                    }
                }
            }

            Log.d(LOG_TAG, "Generated map:")
            map.forEach { Log.d(LOG_TAG, "$it") }
            Log.d(LOG_TAG, "Total sustainability score: $total")

            return@withContext map
        }
    }

    private fun calculateDriverRouteSustainabilityScore(driver: Driver, route: Route): Double {
        var sustainabilityScore = 0.0
        if (route.addressName.lengthIsEven()) {
            // Get the number of vowels in the driver's name
            val count = driver.name.count { char ->
                return@count char.isVowel()
            }
            sustainabilityScore += count * 1.5
        } else {
            // Get the number of consonants in the drivers name
            sustainabilityScore = driver.name.count { char ->
                return@count !char.isVowel()
            }.toDouble()
        }

        // Get the greatest common factor between route address name and driver name.
        val gcd = driver.name.length.gcd(route.addressName.length)
        if (gcd > 1) { // Only apply the 50% if the GCD is not 1
            sustainabilityScore += sustainabilityScore * 0.5
        }
        return sustainabilityScore
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AcmeAlgoModule {

    @Singleton
    @Binds
    abstract fun bindAcmeAlgo(acmeAlgoImpl: AcmeAlgoImpl): AcmeAlgo
}
