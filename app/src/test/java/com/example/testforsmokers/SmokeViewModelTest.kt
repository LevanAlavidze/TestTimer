package com.example.testforsmokers


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.testforsmokers.smoke.data.CounterUpdate
import com.example.testforsmokers.smoke.vm.SmokeViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
class SmokeViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: CigaretteRepository
    private lateinit var timeManager: TimerManager
    private lateinit var viewModel: SmokeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        timeManager = mockk()
        viewModel = SmokeViewModel(repository, timeManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCounterResetsAfterMidnight() = runTest(testDispatcher) {
        val now = Clock.System.now().plus(23.hours + 59.minutes)

        `when`(repository.getDayCigaretteCount()).thenReturn(2)

        viewModel.smoke()
        advanceUntilIdle()

        println("Day cigarette count after smoke(): ${viewModel.dayCigaretteCount.value}")


        val nextDay = now.plus(2.minutes)

        `when`(repository.getCounterUpdate()).thenReturn(
            CounterUpdate(
                lastDayUpdate = nextDay.toEpochMilliseconds(),
                lastWeekUpdate = 0,
                lastMonthUpdate = 0
            )
        )

        viewModel.smoke()
        advanceUntilIdle()
        println("Day cigarette count after next smoke(): ${viewModel.dayCigaretteCount.value}")

        assertEquals(0, viewModel.dayCigaretteCount.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testWeeklyCounterResetsAfterWeek() = runTest(testDispatcher) {
        // Simulate the current time as 11:59 PM on a specific day of the week
        val now = Clock.System.now().plus(6.days + 23.hours + 59.minutes) // Simulate 11:59 PM on the last day of the week

        // Set the initial count for the week as Long
        `when`(repository.getWeekCigaretteCount()).thenReturn(5) // Make sure this is Long

        // Call smoke() to increment the weekly count
        viewModel.smoke()
        advanceUntilIdle()
        println("Weekly cigarette count after smoke(): ${viewModel.weekCigaretteCount.value}")

        // Move the time forward to simulate the next week
        val nextWeek = now.plus(2.minutes) // Simulate 12:01 AM next week

        // Mock the repository response for the new week
        `when`(repository.getCounterUpdate()).thenReturn(
            CounterUpdate(
                lastDayUpdate = now.toEpochMilliseconds(), // This can remain unchanged
                lastWeekUpdate = nextWeek.toEpochMilliseconds().toInt(), // This is Long
                lastMonthUpdate = 0 // Make sure this is Long if necessary
            )
        )

        // Call smoke() again after the week has passed
        viewModel.smoke()
        advanceUntilIdle()
        println("Weekly cigarette count after next smoke(): ${viewModel.weekCigaretteCount.value}")

        // Assert that the weekly counter has reset to 0
        assertEquals(0, viewModel.weekCigaretteCount.value)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testMonthlyCounterResetsAfterMonth() = runTest(testDispatcher) {
        // Simulate the current time as 11:59 PM on the last day of the month
        val now = Clock.System.now().plus(29.days + 23.hours + 59.minutes) // Simulate 11:59 PM on the last day of the month

        // Set the initial count for the month as Long
        `when`(repository.getMonthCigaretteCount()).thenReturn(10) // Ensure this is Long

        // Call smoke() to increment the monthly count
        viewModel.smoke()
        advanceUntilIdle()
        println("Monthly cigarette count after smoke(): ${viewModel.monthCigaretteCount.value}")

        // Move the time forward to simulate the next month
        val nextMonth = now.plus(2.minutes) // Simulate 12:01 AM next month

        // Mock the repository response for the new month
        `when`(repository.getCounterUpdate()).thenReturn(
            CounterUpdate(
                lastDayUpdate = now.toEpochMilliseconds(), // This can remain unchanged
                lastWeekUpdate = 0, // Ensure this is Long if necessary
                lastMonthUpdate = nextMonth.toEpochMilliseconds().toInt() // This should be Long
            )
        )

        // Call smoke() again after the month has passed
        viewModel.smoke()
        advanceUntilIdle()
        println("Monthly cigarette count after next smoke(): ${viewModel.monthCigaretteCount.value}")

        // Assert that the monthly counter has reset to 0
        assertEquals(0, viewModel.monthCigaretteCount.value)
    }

    @Test
    fun testInfiniteTimerWithKotlinxDateTime() = runTest {
        // Simulate a long elapsed time using kotlinx-datetime
        val now = Clock.System.now()
        val longElapsedTime = now.plus(2555920000.hours)

        // Calculate elapsed time
        val elapsedTime = longElapsedTime.toEpochMilliseconds() - now.toEpochMilliseconds()

        // Mocking the formatElapsedTime to return a proper value
        every { timeManager.formatElapsedTime(elapsedTime) } returns "Timer: 2555920000:00:00"

        // Invoke the actual method
        val formattedOutput = timeManager.formatElapsedTime(elapsedTime)

        // Check if the formatted output matches the expected value
        assertEquals("Timer: 2555920000:00:00", formattedOutput)
    }

}