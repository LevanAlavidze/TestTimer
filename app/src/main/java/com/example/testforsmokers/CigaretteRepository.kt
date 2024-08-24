package com.example.testforsmokers

import com.example.testforsmokers.smoke.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
interface CigaretteRepository {
    suspend fun getDayCigaretteCount(): Int
    suspend fun getWeekCigaretteCount(): Int
    suspend fun getMonthCigaretteCount(): Int
    suspend fun saveCigaretteEntry(cigaretteEntry: Cigarette)
    suspend fun saveTimer(timer: Timer)
    suspend fun getTimer(): Timer?
    suspend fun getCounterUpdate(): CounterUpdate?
    suspend fun insertCounterUpdate(counterUpdate: CounterUpdate)
    suspend fun updateCounterUpdate(counterUpdate: CounterUpdate)
}
