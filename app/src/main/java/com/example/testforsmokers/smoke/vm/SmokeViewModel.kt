package com.example.testforsmokers.smoke.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testforsmokers.CigaretteRepository
import com.example.testforsmokers.TimerManager
import com.example.testforsmokers.smoke.data.Cigarette
import com.example.testforsmokers.smoke.data.CounterUpdate
import com.example.testforsmokers.smoke.data.Timer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SmokeViewModel @Inject constructor(
    private val repository: CigaretteRepository,
    private val timerManager: TimerManager
) : ViewModel() {

    private val _dayCigaretteCount = MutableLiveData<Int>()
    val dayCigaretteCount: LiveData<Int> get() = _dayCigaretteCount

    private val _weekCigaretteCount = MutableLiveData<Int>()
    val weekCigaretteCount: LiveData<Int> get() = _weekCigaretteCount

    private val _monthCigaretteCount = MutableLiveData<Int>()
    val monthCigaretteCount: LiveData<Int> get() = _monthCigaretteCount

    private val _timerText = MutableLiveData<String>()
    val timerText: LiveData<String> get() = _timerText


    init {
        loadTimerState()
        updateCigaretteCount()
    }

    fun smoke() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            timerManager.resetAndStartTimer { formattedTime ->
                _timerText.postValue(formattedTime)
            }
            repository.saveTimer(Timer(startTime = startTime, isRunning = true))
            repository.saveCigaretteEntry(Cigarette(timestamp = startTime))
            updateCigaretteCount()
        }
    }

    private fun loadTimerState() {
        viewModelScope.launch {
            val timer = repository.getTimer()
            if (timer != null && timer.isRunning) {
                timerManager.resumeTimer(timer.startTime) { formattedTime ->
                    _timerText.postValue(formattedTime)
                }
            } else {
                _timerText.value = "Timer: 00:00:00"
            }
        }
    }
    // Helper function to calculate week of the year
    private fun LocalDate.getWeekOfYear(): Int {
        val localDate = java.time.LocalDate.of(this.year, this.monthNumber, this.dayOfMonth)
        return localDate.get(WeekFields.of(Locale.getDefault()).weekOfYear())
    }

    private fun updateCigaretteCount() {
        viewModelScope.launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val currentWeek = today.getWeekOfYear()
            val currentMonth = today.monthNumber
            var counterUpdate = repository.getCounterUpdate()

            if (counterUpdate == null) {
                counterUpdate = CounterUpdate(
                    lastDayUpdate = Clock.System.now().toEpochMilliseconds(),
                    lastWeekUpdate = currentWeek,
                    lastMonthUpdate = currentMonth
                )
                repository.insertCounterUpdate(counterUpdate)
            }

            var updatedCounterUpdate = counterUpdate

            // Reset day count if the day has changed
            val lastDayUpdate = Instant.fromEpochMilliseconds(counterUpdate.lastDayUpdate)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date

            if (today != lastDayUpdate) {
                _dayCigaretteCount.value = 0
                updatedCounterUpdate = updatedCounterUpdate.copy(lastDayUpdate = Clock.System.now().toEpochMilliseconds())
            } else {
                _dayCigaretteCount.value = repository.getDayCigaretteCount()
            }

            // Reset week count if the week has changed
            if (currentWeek != counterUpdate.lastWeekUpdate) {
                _weekCigaretteCount.value = 0
                updatedCounterUpdate = updatedCounterUpdate.copy(lastWeekUpdate = currentWeek)
            } else {
                _weekCigaretteCount.value = repository.getWeekCigaretteCount()
            }

            // Reset month count if the month has changed
            if (currentMonth != counterUpdate.lastMonthUpdate) {
                _monthCigaretteCount.value = 0
                updatedCounterUpdate = updatedCounterUpdate.copy(lastMonthUpdate = currentMonth)
            } else {
                _monthCigaretteCount.value = repository.getMonthCigaretteCount()
            }

            // Save updated counter state
            repository.updateCounterUpdate(updatedCounterUpdate)
        }
    }
}


