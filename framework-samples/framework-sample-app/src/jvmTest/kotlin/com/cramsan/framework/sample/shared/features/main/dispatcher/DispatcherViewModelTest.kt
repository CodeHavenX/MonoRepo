package com.cramsan.framework.sample.shared.features.main.dispatcher

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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DispatcherViewModelTest : CoroutineTest() {

    private lateinit var viewModel: DispatcherViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel = DispatcherViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
        )
    }

    @Test
    fun `initial UIState is correct`() = runCoroutineTest {
        assertEquals(DispatcherUIState.Initial, viewModel.uiState.value)
        assertEquals(DispatcherUIState.Initial.ioDispatcherInfo, viewModel.uiState.value.ioDispatcherInfo)
        assertEquals(DispatcherUIState.Initial.uiDispatcherInfo, viewModel.uiState.value.uiDispatcherInfo)
    }

    @Test
    fun `queryIoDispatcher updates ioDispatcherInfo`() = runCoroutineTest {
        viewModel.queryIoDispatcher()

        assertNotEquals("(not queried)", viewModel.uiState.value.ioDispatcherInfo)
    }

    @Test
    fun `queryUiDispatcher updates uiDispatcherInfo`() = runCoroutineTest {
        viewModel.queryUiDispatcher()

        assertNotEquals("(not queried)", viewModel.uiState.value.uiDispatcherInfo)
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
