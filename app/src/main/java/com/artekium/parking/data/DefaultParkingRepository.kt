package com.artekium.parking.data

import com.artekium.parking.data.source.local.Entry
import com.artekium.parking.data.source.local.Parking
import com.artekium.parking.data.source.local.ParkingDao
import com.artekium.parking.data.source.local.ParkingWithEntries
import com.artekium.parking.data.source.local.Vehicle
import com.artekium.parking.data.source.local.VehicleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultParkingRepository @Inject constructor(
    private val vehiclesLocalSource: VehicleDao,
    private val parkingLocalSource: ParkingDao
) : ParkingRepository {
    override suspend fun startWorkDay(capacity: Int): ParkingWithEntries =
        withContext(Dispatchers.IO) {
            with(Parking(capacity = capacity)) {
                parkingLocalSource.upsertParking(this).let { id ->
                    parkingLocalSource.getParkingWithEntries(id)
                }
            }
        }

    override suspend fun refreshActualWorkDay(): ParkingWithEntries = withContext(Dispatchers.IO) {
        parkingLocalSource.findActualWorkDay()?.let { parking ->
            parkingLocalSource.getParkingWithEntries(parking.id)
        } ?: startWorkDay()
    }

    override suspend fun saveVehicle(
        license: String,
        brand: String?,
        model: String?,
        color: String?
    ) = withContext(Dispatchers.IO) {
        delay(5000)
        val vehicle = Vehicle(license, brand, model, color)
        vehiclesLocalSource.upsertVehicle(vehicle)
    }

    override suspend fun enterVehicle(license: String) = withContext(Dispatchers.IO) {
        delay(5000)
        parkingLocalSource.findActualWorkDay()?.let { parking ->
            val capacity = parking.capacity
            if (capacity > 0) {
                // Registrar la entrada del vehiculo
                with(Entry(parking = parking.id, vehicle = license)) {
                    parkingLocalSource.insertEntry(this)
                }

                // Actualizar la capacidad libre del estacionamiento
                parking.copy(
                    capacity = capacity.dec()
                ).also {
                    parkingLocalSource.upsertParking(it)
                }

                // Refrescar la información
                parkingLocalSource.getParkingWithEntries(parking.id)
            } else {
                throw Exception("Not capacity")
            }
        } ?: throw Exception("Please start system")
    }

    override suspend fun exitVehicle(license: String) = withContext(Dispatchers.IO) {
        delay(5000)
        parkingLocalSource.findActualWorkDay()?.let { parking ->

            // Actualizar la entrada del vehículo
            parkingLocalSource.findEntryByParkingAndVehicle(parking.id, license)?.copy(
                completed = true,
                exitDate = Calendar.getInstance().time.time
            )?.also { entry ->
                parkingLocalSource.updateEntry(entry)
            }

            // Actualizar la capacidad libre del estacionamiento
            parking.copy(
                capacity = parking.capacity.inc()
            ).also {
                parkingLocalSource.upsertParking(it)
            }

            // Refrescar la información
            parkingLocalSource.getParkingWithEntries(parking.id)
        } ?: throw Exception("Please start system")
    }

    override suspend fun calculateTotal(license: String): Double {
        TODO("Not yet implemented")
    }

}