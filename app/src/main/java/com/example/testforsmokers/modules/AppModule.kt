package com.example.testforsmokers.modules

import android.app.Application
import androidx.room.Room
import com.example.testforsmokers.CigaretteDatabase
import com.example.testforsmokers.smoke.data.CigaretteDao
import com.example.testforsmokers.smoke.data.CounterUpdateDao
import com.example.testforsmokers.smoke.data.TimerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): CigaretteDatabase {
        return Room.databaseBuilder(
            app,
            CigaretteDatabase::class.java,
            "cigarette_database"
        ).build()
    }

    @Provides
    fun provideCigaretteDao(db: CigaretteDatabase): CigaretteDao {
        return db.cigaretteDao()
    }
    @Provides
    fun provideTimerDao(db: CigaretteDatabase): TimerDao {
        return db.timerDao()
    }
    @Provides
    fun provideCounterUpdateDao(db: CigaretteDatabase): CounterUpdateDao {
        return db.counterUpdateDao()
    }

}