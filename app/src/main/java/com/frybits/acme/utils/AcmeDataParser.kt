package com.frybits.acme.utils

import com.frybits.acme.models.Driver
import com.frybits.acme.models.DwellingType
import com.frybits.acme.models.Route
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private const val SHIPMENTS_KEY = "shipments"
private const val DRIVERS_KEY = "drivers"

interface AcmeDataParser {

    suspend fun parseJson(jsonObject: JSONObject): Pair<List<Driver>, List<Route>>
}

class AcmeDataParserImpl @Inject constructor(): AcmeDataParser {

    // It was unclear if the entire address was to be used, or just the name, so I made a quick regex to split it all, just in case it is only name
    private val addressRegex = "^(?<addressNumber>\\d*)\\s(?<addressName>\\w*\\s\\w*)\\s?(?<dwellingType>Suite|Apt\\.)?\\s?(?<dwellingNumber>\\w*)?\$".toRegex()

    override suspend fun parseJson(jsonObject: JSONObject): Pair<List<Driver>, List<Route>> {
        // Do this work on a background thread
        return withContext(Dispatchers.Default) {

            // Perform these tasks in parallel
            val shipmentsJob = async {
                return@async buildList {
                    val array = jsonObject.getJSONArray(SHIPMENTS_KEY)
                    repeat(array.length()) {
                        val address = array.getString(it)

                        addressRegex.matchEntire(address)?.let { result ->
                            val (addressNumber, addressName, dwellingType, dwellingNumber) = result.destructured
                            val sanitizedAddressNumber = addressNumber.toIntOrNull() ?: return@repeat
                            val sanitizedAddressName = if (addressName.isNotBlank()) addressName else return@repeat
                            val sanitizedDwellingType = DwellingType.getType(dwellingType)
                            val sanitizedDwellingNumber = dwellingNumber.toIntOrNull()
                            add(Route(sanitizedAddressNumber, sanitizedAddressName, sanitizedDwellingType, sanitizedDwellingNumber))
                        }
                    }
                }
            }

            val driversJob = async {
                return@async buildList {
                    val array = jsonObject.getJSONArray(DRIVERS_KEY)
                    repeat(array.length()) {
                        val name = array.getString(it)
                        add(Driver(name))
                    }
                }
            }

            return@withContext driversJob.await() to shipmentsJob.await()
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AcmeDataParserModule {

    @Singleton
    @Binds
    abstract fun bindAcmeDataParser(acmeDataParserImpl: AcmeDataParserImpl): AcmeDataParser
}
