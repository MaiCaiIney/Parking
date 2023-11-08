package com.artekium.parking.data.source.local

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import java.util.Calendar

@Entity
data class Parking(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val capacity: Int,
    val startedAt: Long = Calendar.getInstance().time.time,
    val endedAt: Long? = null,
    val gain: Double = 0.0,
    val finished: Boolean = false
)

@Entity
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val parking: Long,
    val vehicle: String,
    val entryDate: Long = Calendar.getInstance().time.time,
    val exitDate: Long? = null,
    val price: Double = 0.0,
    val completed: Boolean = false
)

data class ParkingWithEntries(
    @Embedded val parking: Parking,
    @Relation(
        parentColumn = "id",
        entityColumn = "parking"
    )
    val entries: List<Entry>
)

@Dao
interface ParkingDao {
    @Upsert
    fun upsertParking(parking: Parking): Long

    @Query("SELECT * FROM parking WHERE finished = 0") // 1: false
    fun findActualWorkDay(): Parking?

    @Transaction
    @Query("SELECT * FROM parking WHERE id = :id")
    fun getParkingWithEntries(id: Long): ParkingWithEntries

    @Insert
    fun insertEntry(entry: Entry)

    @Update
    fun updateEntry(entry: Entry)

    @Query("SELECT * FROM entry WHERE parking = :parking AND vehicle = :license")
    fun findEntryByParkingAndVehicle(parking: Long, license: String): Entry?
}