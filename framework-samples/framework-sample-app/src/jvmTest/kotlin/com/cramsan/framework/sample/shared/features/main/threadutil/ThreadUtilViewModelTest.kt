package com.cramsan.framework.sample.shared.features.main.threadutil

import app.cash.turbine.turbineScope
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import com.cramsan.framework.thread.ThreadUtilInterface
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ThreadUtilViewModelTest : CoroutineTest() {

    companion object {
        private const val EXPECTED_ASSERT_UI_THREAD_ACTION = "assertIsUIThread() called (check logs for result)"
        private const val EXPECTED_ASSERT_BG_THREAD_ACTION = "assertIsBackgroundThread() called (check logs for result)"
    }

    private lateinit var threadUtil: ThreadUtilInterface
    private lateinit var viewModel: ThreadUtilViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        threadUtil = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel = ThreadUtilViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            threadUtil = threadUtil,
        )
    }

    @Test
    fun `initial UIState is correct`() = runCoroutineTest {
        assertEquals(ThreadUtilUIState.Initial, viewModel.uiState.value)
        assertNull(viewModel.uiState.value.isUIThread)
        assertNull(viewModel.uiState.value.isBackgroundThread)
        assertEquals(ThreadUtilUIState.Initial.lastAction, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `checkIsUIThread updates isUIThread and lastAction`() = runCoroutineTest {
        val mockResult = true
        every { threadUtil.isUIThread() } returns mockResult

        viewModel.checkIsUIThread()

        assertTrue(viewModel.uiState.value.isUIThread == mockResult)
        assertEquals("isUIThread() → $mockResult", viewModel.uiState.value.lastAction)
    }

    @Test
    fun `checkIsBackgroundThread updates isBackgroundThread and lastAction`() = runCoroutineTest {
        val mockResult = false
        every { threadUtil.isBackgroundThread() } returns mockResult

        viewModel.checkIsBackgroundThread()

        assertEquals(mockResult, viewModel.uiState.value.isBackgroundThread)
        assertEquals("isBackgroundThread() → $mockResult", viewModel.uiState.value.lastAction)
    }

    @Test
    fun `assertIsUIThread calls threadUtil assertIsUIThread and updates lastAction`() = runCoroutineTest {
        justRun { threadUtil.assertIsUIThread() }

        viewModel.assertIsUIThread()

        verify { threadUtil.assertIsUIThread() }
        assertEquals(EXPECTED_ASSERT_UI_THREAD_ACTION, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `assertIsBackgroundThread calls threadUtil assertIsBackgroundThread and updates lastAction`() = runCoroutineTest {
        justRun { threadUtil.assertIsBackgroundThread() }

        viewModel.assertIsBackgroundThread()

        verify { threadUtil.assertIsBackgroundThread() }
        assertEquals(EXPECTED_ASSERT_BG_THREAD_ACTION, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `navigateBack emits NavigateBack event`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateBack()

            assertEquals(SampleWindowEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
