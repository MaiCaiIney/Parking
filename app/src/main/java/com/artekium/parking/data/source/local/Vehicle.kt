package com.artekium.parking.data.source.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert

@Entity
data class Vehicle(
    @PrimaryKey val license: String,
    val brand: String?,
    val model: String?,
    val color: String?
)

@Dao
interface VehicleDao {
    @Upsert
    fun upsertVehicle(vehicle: Vehicle)

    @Query("SELECT * FROM vehicle")
    fun loadAllVehicles(): Array<Vehicle>

    @Query("SELECT * FROM vehicle WHERE license = :license")
    fun getVehicleByLicense(license: String): Vehicle
}

