package com.example.testforsmokers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.testforsmokers.smoke.data.CounterUpdate
import com.example.testforsmokers.smoke.vm.SmokeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*

@ExperimentalCoroutinesApi
class SmokeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: CigaretteRepository

    private lateinit var viewModel: SmokeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = SmokeViewModel(repository)
    }

    @Test
    fun testDailyCounterReset() = runTest(testDispatcher) {
        val currentTime = System.currentTimeMillis()
        val currentDay = Calendar.getInstance().apply { timeInMillis = currentTime }
        val yesterdayTime = currentTime - (24 * 60 * 60 * 1000) // 1 day before

        val counterUpdate = CounterUpdate(
            lastDayUpdate = yesterdayTime,
            lastWeekUpdate = currentDay.get(Calendar.WEEK_OF_YEAR),
            lastMonthUpdate = currentDay.get(Calendar.MONTH)
        )

        `when`(repository.getCounterUpdate()).thenReturn(counterUpdate)
        `when`(repository.getDayCigaretteCount()).thenReturn(5)
        `when`(repository.getWeekCigaretteCount()).thenReturn(10)
        `when`(repository.getMonthCigaretteCount()).thenReturn(20)

        viewModel.smoke() // Trigger a smoke action which should update the counters

        // Verify that the day count is reset
        verify(repository).insertCounterUpdate(
            counterUpdate.copy(
                lastDayUpdate = currentTime
            )
        )

        verify(repository).getDayCigaretteCount()
    }

    @Test
    fun testWeeklyCounterReset() = runTest(testDispatcher) {
        val currentTime = System.currentTimeMillis()
        val currentWeek = Calendar.getInstance().apply { timeInMillis = currentTime }.get(Calendar.WEEK_OF_YEAR)
        val lastWeek = currentWeek - 1

        val counterUpdate = CounterUpdate(
            lastDayUpdate = currentTime,
            lastWeekUpdate = lastWeek,
            lastMonthUpdate = Calendar.getInstance().get(Calendar.MONTH)
        )

        `when`(repository.getCounterUpdate()).thenReturn(counterUpdate)
        `when`(repository.getWeekCigaretteCount()).thenReturn(10)
        `when`(repository.getMonthCigaretteCount()).thenReturn(20)

        viewModel.smoke()

        // Verify that the week count is reset
        verify(repository).insertCounterUpdate(
            counterUpdate.copy(
                lastWeekUpdate = currentWeek
            )
        )

        verify(repository).getWeekCigaretteCount()
    }

    @Test
    fun testMonthlyCounterReset() = runTest(testDispatcher) {
        val currentTime = System.currentTimeMillis()
        val currentMonth = Calendar.getInstance().apply { timeInMillis = currentTime }.get(Calendar.MONTH)
        val lastMonth = currentMonth - 1

        val counterUpdate = CounterUpdate(
            lastDayUpdate = currentTime,
            lastWeekUpdate = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR),
            lastMonthUpdate = lastMonth
        )

        `when`(repository.getCounterUpdate()).thenReturn(counterUpdate)
        `when`(repository.getMonthCigaretteCount()).thenReturn(20)

        viewModel.smoke()

        // Verify that the month count is reset
        verify(repository).insertCounterUpdate(
            counterUpdate.copy(
                lastMonthUpdate = currentMonth
            )
        )

        verify(repository).getMonthCigaretteCount()
    }
}
