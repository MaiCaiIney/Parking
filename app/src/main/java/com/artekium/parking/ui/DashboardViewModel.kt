package com.artekium.parking.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.artekium.parking.data.ParkingRepository
import com.artekium.parking.data.source.local.ParkingWithEntries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val parkingRepository: ParkingRepository
) : ViewModel() {

    private val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val _parking = MutableLiveData<ParkingWithEntries>()

    val capacity: LiveData<Int> = _parking.switchMap { parking ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(parking.parking.capacity)
        }
    }

    val started: LiveData<Boolean> = _parking.switchMap { parking ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(!parking.parking.finished)
        }
    }

    val entries: LiveData<List<DashboardItem>> = _parking.switchMap { parking ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            parking.entries.map { entry ->
                DashboardItem(
                    license = entry.vehicle,
                    startedAt = formatter.format(Date(entry.entryDate)),
                    finished = entry.completed
                )
            }.also { emit(it) }
        }
    }

    fun startParking(capacity: Int) = viewModelScope.launch {
        _parking.value = parkingRepository.startWorkDay(capacity)
    }

    fun enterVehicle(
        license: String,
        brand: String? = null,
        model: String? = null,
        color: String? = null
    ) =
        viewModelScope.launch {
            parkingRepository.saveVehicle(license, brand, model, color)
            _parking.value = parkingRepository.enterVehicle(license)
        }

    fun exitVehicle(license: String) = viewModelScope.launch {
        _parking.value = parkingRepository.exitVehicle(license)
    }

}