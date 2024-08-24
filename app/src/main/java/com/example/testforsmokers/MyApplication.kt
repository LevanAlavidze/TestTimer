package com.example.testforsmokers

import android.app.Application
import androidx.room.Room
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {
    @Inject
    lateinit var cigaretteRepository: CigaretteRepository
    override fun onCreate() {
        super.onCreate()
    Room.databaseBuilder(
        applicationContext,
        CigaretteDatabase::class.java,
        "cigarette_database"
    ).build()
    }
}