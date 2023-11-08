package com.artekium.parking.data

import com.artekium.parking.data.source.local.ParkingWithEntries

interface ParkingRepository {

    suspend fun startWorkDay(capacity: Int = 0): ParkingWithEntries

    suspend fun refreshActualWorkDay(): ParkingWithEntries

    suspend fun saveVehicle(license: String, brand: String?, model: String?, color: String?)

    suspend fun enterVehicle(license: String): ParkingWithEntries

    suspend fun exitVehicle(license: String): ParkingWithEntries

    suspend fun calculateTotal(license: String): Double
}