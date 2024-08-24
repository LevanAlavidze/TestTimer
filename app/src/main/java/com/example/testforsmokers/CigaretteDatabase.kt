package com.example.testforsmokers

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.testforsmokers.smoke.data.Cigarette
import com.example.testforsmokers.smoke.data.CigaretteDao
import com.example.testforsmokers.smoke.data.CounterUpdate
import com.example.testforsmokers.smoke.data.CounterUpdateDao
import com.example.testforsmokers.smoke.data.Timer
import com.example.testforsmokers.smoke.data.TimerDao

@Database(entities = [Cigarette::class, Timer::class, CounterUpdate::class],
    version = 4,
    autoMigrations =[AutoMigration(from = 3,to =4)]
)
abstract class CigaretteDatabase : RoomDatabase() {
    abstract fun cigaretteDao(): CigaretteDao
    abstract fun timerDao(): TimerDao
    abstract fun counterUpdateDao(): CounterUpdateDao
}