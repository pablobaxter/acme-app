package com.frybits.acme.repo

import android.content.Context
import android.util.Log
import com.frybits.acme.R
import com.frybits.acme.models.Driver
import com.frybits.acme.models.Route
import com.frybits.acme.utils.AcmeAlgo
import com.frybits.acme.utils.DriverParser
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private const val LOG_TAG = "AcmeRepo"

interface AcmeRepo {

    suspend fun initialize()

    suspend fun getDrivers(): List<Driver>

    suspend fun getRouteAndScoreForDriver(driver: Driver): Pair<Double, Route>
}

class AcmeRepoImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val acmeAlgo: AcmeAlgo,
    private val driverParser: DriverParser
) : AcmeRepo {

    // Suspends anything needing this data until it completes, and acts as a cache as well.
    private val driverRouteMapJob = CompletableDeferred<Map<Driver, Pair<Double, Route>>>()

    // Normally, this wouldn't be necessary, especially if this repository is backed by a network layer.
    // However, due to the usage of suspend, this function was necessary to load the data.
    override suspend fun initialize() {
        // Do IO work in IO thread
        val json = withContext(Dispatchers.IO) { JSONObject(context.resources.openRawResource(R.raw.input).bufferedReader().readText()) }
        Log.d(LOG_TAG, "$json")
        val (driverList, routeList) = driverParser.parseJson(json)

        require(driverList.size == routeList.size) { "Driver-Route list size don't match" }

        val map = acmeAlgo.generateDriverRouteMap(driverList, routeList)

        driverRouteMapJob.complete(map)
    }

    override suspend fun getDrivers(): List<Driver> {
        return driverRouteMapJob.await().keys.toList()
    }

    override suspend fun getRouteAndScoreForDriver(driver: Driver): Pair<Double, Route> {
        val map = driverRouteMapJob.await()
        return map[driver] ?: throw IllegalStateException("Route for driver ${driver.name} does not exist")
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AcmeRepoModule {

    @Singleton
    @Binds
    abstract fun bindAcmeRepo(acmeRepoImpl: AcmeRepoImpl): AcmeRepo
}
