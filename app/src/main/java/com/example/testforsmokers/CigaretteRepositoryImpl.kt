package com.example.testforsmokers

import com.example.testforsmokers.smoke.data.Cigarette
import com.example.testforsmokers.smoke.data.CigaretteDao
import com.example.testforsmokers.smoke.data.CounterUpdate
import com.example.testforsmokers.smoke.data.CounterUpdateDao
import com.example.testforsmokers.smoke.data.Timer
import com.example.testforsmokers.smoke.data.TimerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CigaretteRepositoryImpl @Inject constructor(
    private val cigaretteDao: CigaretteDao,
    private val timerDao: TimerDao,
    private val counterUpdateDao: CounterUpdateDao
) : CigaretteRepository {

    override suspend fun getDayCigaretteCount(): Int = withContext(Dispatchers.IO) {
        cigaretteDao.getDayCigaretteCount()
    }

    override suspend fun getWeekCigaretteCount(): Int = withContext(Dispatchers.IO) {
        cigaretteDao.getWeekCigaretteCount()
    }

    override suspend fun getMonthCigaretteCount(): Int = withContext(Dispatchers.IO) {
        cigaretteDao.getMonthCigaretteCount()
    }

    override suspend fun saveCigaretteEntry(cigaretteEntry: Cigarette) = withContext(Dispatchers.IO) {
        cigaretteDao.insert(cigaretteEntry)
    }

    override suspend fun saveTimer(timer: Timer) = withContext(Dispatchers.IO) {
        timerDao.insertOrUpdate(timer)
    }

    override suspend fun getTimer(): Timer? = withContext(Dispatchers.IO) {
        timerDao.getTimer()
    }

    override suspend fun getCounterUpdate(): CounterUpdate? = withContext(Dispatchers.IO) {
        counterUpdateDao.getCounterUpdate()
    }

    override suspend fun insertCounterUpdate(counterUpdate: CounterUpdate) = withContext(Dispatchers.IO) {
        counterUpdateDao.insert(counterUpdate)
    }

    override suspend fun updateCounterUpdate(counterUpdate: CounterUpdate) = withContext(Dispatchers.IO) {
        counterUpdateDao.update(counterUpdate)
    }
}
