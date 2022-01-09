package com.frybits.acme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frybits.acme.models.Driver
import com.frybits.acme.models.Route
import com.frybits.acme.repo.AcmeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for driver data.
 */
@HiltViewModel
class DriverViewModel @Inject constructor(private val acmeRepo: AcmeRepo) : ViewModel() {

    var lastSelectedDriver: Driver? = null
        private set

    init {
        viewModelScope.launch { acmeRepo.initialize() }
    }

    suspend fun getDriverList(): List<Driver> {
        return acmeRepo.getDrivers()
    }

    suspend fun getRouteAndScoreForDriver(driver: Driver): Pair<Double, Route> {
        lastSelectedDriver = driver
        return acmeRepo.getRouteAndScoreForDriver(driver)
    }

    fun resetLastSelectedDriver() {
        lastSelectedDriver = null
    }
}
