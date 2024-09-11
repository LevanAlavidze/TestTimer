package com.example.testforsmokers

import kotlinx.coroutines.*
import java.util.Locale
import javax.inject.Inject

class TimerManager @Inject constructor(
    private val dispatcher: CoroutineDispatcher
) {

    private var timerJob: Job? = null
    private var timerStartTime: Long = 0L

    fun resetAndStartTimer(onTimerUpdate: (String) -> Unit) {
        timerStartTime = System.currentTimeMillis()
        startTimer(onTimerUpdate)
    }

    private fun startTimer(onTimerUpdate: (String) -> Unit) {
        timerJob?.cancel() // Cancel any existing timer
        timerJob = Job()
        val job = timerJob!!

        CoroutineScope(dispatcher + job).launch {
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - timerStartTime
                val formattedTime = formatElapsedTime(elapsedTime)
                withContext(dispatcher) {
                    onTimerUpdate(formattedTime)
                }
                delay(1000) // Update every second
            }
        }
    }

    fun resumeTimer(startTime: Long, onTimerUpdate: (String) -> Unit) {
        timerStartTime = startTime
        startTimer(onTimerUpdate)
    }


    private fun formatElapsedTime(elapsedTime: Long): String {
        val seconds = (elapsedTime / 1000) % 60
        val minutes = (elapsedTime / (1000 * 60)) % 60
        val hours = (elapsedTime / (1000 * 60 * 60)) % 24
        return String.format(Locale.getDefault(), "Timer: %02d:%02d:%02d", hours, minutes, seconds)
    }
}
