package com.example.testforsmokers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.testforsmokers.smoke.data.CounterUpdate
import com.example.testforsmokers.smoke.vm.SmokeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class SmokeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: CigaretteRepository

    @Mock
    private lateinit var timerManager: TimerManager

    private lateinit var viewModel: SmokeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = SmokeViewModel(repository, timerManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testDailyCounterReset() = runTest(testDispatcher) {
        // Setup initial conditions for the test
        val currentTime = System.currentTimeMillis()
        val counterUpdate = CounterUpdate(
            lastDayUpdate = currentTime - (24 * 60 * 60 * 1000), // 1 day before
            lastWeekUpdate = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR),
            lastMonthUpdate = Calendar.getInstance().get(Calendar.MONTH)
        )

        // Mock repository responses
        `when`(repository.getCounterUpdate()).thenReturn(counterUpdate)
        `when`(repository.getDayCigaretteCount()).thenReturn(5)

        // Call the method in the ViewModel you want to test
        viewModel.smoke()

        // Let the test dispatcher process any pending coroutines
        advanceUntilIdle()

        // Check if insertCounterUpdate was called with any non-null CounterUpdate
        verify(repository).insertCounterUpdate(any(CounterUpdate::class.java))

        // Capture the argument passed to insertCounterUpdate
        val captor = ArgumentCaptor.forClass(CounterUpdate::class.java)
        verify(repository).insertCounterUpdate(captor.capture())

        // Assert that the captured value is not null
        val capturedUpdate = captor.value
        assertNotNull(capturedUpdate)

        // Assert that lastDayUpdate was updated to a recent timestamp
        assertTrue(capturedUpdate.lastDayUpdate in (currentTime - 1000)..(currentTime + 1000))

        // Verify other expected method calls
        verify(repository).getDayCigaretteCount()
    }

}