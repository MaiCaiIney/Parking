package com.artekium.parking.di

import android.content.Context
import androidx.room.Room
import com.artekium.parking.data.DefaultParkingRepository
import com.artekium.parking.data.ParkingRepository
import com.artekium.parking.data.source.local.AppDatabase
import com.artekium.parking.data.source.local.ParkingDao
import com.artekium.parking.data.source.local.VehicleDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindParkingRepository(repository: DefaultParkingRepository): ParkingRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "Parking.db"
        ).build()
    }

    @Provides
    fun provideVehicleDao(database: AppDatabase): VehicleDao = database.vehicleDao()

    @Provides
    fun provideParkingDao(database: AppDatabase): ParkingDao = database.parkingDao()
}