package com.frybits.acme.viewmodels

import androidx.lifecycle.ViewModel
import com.frybits.acme.models.Driver
import com.frybits.acme.models.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for driver data.
 */

@HiltViewModel
class DriverViewModel @Inject constructor(): ViewModel() {

    var lastSelectedDriver: Driver? = null
        private set

    suspend fun getDriverList(): List<Driver> {
        return emptyList()
    }

    suspend fun getRouteForDriver(driver: Driver): Route {
        lastSelectedDriver = driver
        return Route(0, "0")
    }

    fun resetLastSelectedDriver() {
        lastSelectedDriver = null
    }
}
