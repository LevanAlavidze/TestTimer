package com.example.testforsmokers.smoke.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testforsmokers.CigaretteRepository
import com.example.testforsmokers.smoke.data.Cigarette
import com.example.testforsmokers.smoke.data.CounterUpdate
import com.example.testforsmokers.smoke.data.Timer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SmokeViewModel @Inject constructor(
    private val repository: CigaretteRepository
) : ViewModel() {

    private var timerStartTime: Long = 0L
    private var isTimerRunning: Boolean = false

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
            if (isTimerRunning) {
                resetTimer()
            } else {
                startTimer()
            }
            repository.saveCigaretteEntry(Cigarette(timestamp = System.currentTimeMillis()))
            updateCigaretteCount()
        }
    }

    private fun startTimer() {
        isTimerRunning = true
        timerStartTime = System.currentTimeMillis()
        saveTimerState(Timer(startTime = timerStartTime, isRunning = isTimerRunning))
        updateTimerText()

        viewModelScope.launch {
            while (isTimerRunning && isActive) {
                updateTimerText()
                delay(1000) // Update the timer text every second
            }
        }
    }

    private fun resetTimer() {
        isTimerRunning = false
        _timerText.value = "Timer: 00:00:00" // Reset display to zero
        startTimer() // Restart the timer with the new start time
    }

    private fun loadTimerState() {
        viewModelScope.launch {
            val timer = repository.getTimer()
            if (timer != null) {
                isTimerRunning = timer.isRunning
                if (isTimerRunning) {
                    timerStartTime = timer.startTime
                    resumeTimer()
                } else {
                    _timerText.value = "Timer: 00:00:00"
                }
            }
        }
    }

    private fun resumeTimer() {
        viewModelScope.launch {
            while (isTimerRunning && isActive) {
                updateTimerText()
                delay(1000) // Update the timer text every second
            }
        }
    }

    private fun updateTimerText() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - timerStartTime
        _timerText.value = formatElapsedTime(elapsedTime)
    }

    private fun saveTimerState(timer: Timer) {
        viewModelScope.launch {
            repository.saveTimer(timer)
        }
    }

    private fun formatElapsedTime(elapsedTime: Long): String {
        val seconds = (elapsedTime / 1000) % 60
        val minutes = (elapsedTime / (1000 * 60)) % 60
        val hours = (elapsedTime / (1000 * 60 * 60)) % 24
        return String.format(Locale.getDefault(), "Timer: %02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun updateCigaretteCount() {
        viewModelScope.launch {
            val today = System.currentTimeMillis()
            val currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            var counterUpdate = repository.getCounterUpdate()

            if (counterUpdate == null) {
                counterUpdate = CounterUpdate(
                    lastDayUpdate = today,
                    lastWeekUpdate = currentWeek,
                    lastMonthUpdate = currentMonth
                )
                repository.insertCounterUpdate(counterUpdate)
            }

            var updatedCounterUpdate = counterUpdate

            // Reset day count if the day has changed
            val lastDayUpdate = Calendar.getInstance().apply { timeInMillis = counterUpdate.lastDayUpdate }
            val currentDay = Calendar.getInstance().apply { timeInMillis = today }

            if (currentDay.get(Calendar.DAY_OF_YEAR) != lastDayUpdate.get(Calendar.DAY_OF_YEAR)) {
                _dayCigaretteCount.value = 0
                updatedCounterUpdate = updatedCounterUpdate.copy(lastDayUpdate = today)
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