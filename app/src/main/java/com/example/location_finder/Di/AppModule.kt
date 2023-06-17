package com.example.location_finder.Di

import android.content.Context
import androidx.room.Room
import com.example.location_finder.Dao.LocationDao
import com.example.location_finder.Database.LocationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        LocationDatabase::class.java,
        "locationdata"
    ).build() // The reason we can construct a database for the repo

    @Singleton
    @Provides
    fun provideYourDao(db: LocationDatabase) = db.locationDao()

}