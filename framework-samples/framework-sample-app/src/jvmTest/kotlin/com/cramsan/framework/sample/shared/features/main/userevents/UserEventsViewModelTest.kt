package com.cramsan.framework.sample.shared.features.main.userevents

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
import com.cramsan.framework.userevents.UserEventsInterface
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserEventsViewModelTest : CoroutineTest() {

    companion object {
        private const val SAMPLE_EVENT = "sample_event"
        private const val SAMPLE_EVENT_WITH_METADATA = "sample_event_with_metadata"
        private const val EXPECTED_INITIALIZE_ACTION = "initialize() called"
        private const val EXPECTED_LOG_EVENT_ACTION = "log(tag, \"$SAMPLE_EVENT\") called"
        private const val EXPECTED_LOG_EVENT_WITH_METADATA_ACTION = "log(tag, \"$SAMPLE_EVENT_WITH_METADATA\", metadata) called"
    }

    private lateinit var userEvents: UserEventsInterface
    private lateinit var viewModel: UserEventsViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        userEvents = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel = UserEventsViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            userEvents = userEvents,
        )
    }

    @Test
    fun `initial UIState is correct`() = runCoroutineTest {
        assertEquals(UserEventsUIState.Initial, viewModel.uiState.value)
        assertEquals(UserEventsUIState.Initial.lastAction, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `initialize calls userEvents initialize and updates lastAction`() = runCoroutineTest {
        justRun { userEvents.initialize() }

        viewModel.initialize()

        verify { userEvents.initialize() }
        assertEquals(EXPECTED_INITIALIZE_ACTION, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `logEvent calls userEvents log with sample event and updates lastAction`() = runCoroutineTest {
        justRun { userEvents.log(any(), any()) }

        viewModel.logEvent()

        verify { userEvents.log(any(), SAMPLE_EVENT) }
        assertEquals(EXPECTED_LOG_EVENT_ACTION, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `logEventWithMetadata calls userEvents log with metadata and updates lastAction`() = runCoroutineTest {
        justRun { userEvents.log(any(), any(), any()) }

        viewModel.logEventWithMetadata()

        verify { userEvents.log(any(), SAMPLE_EVENT_WITH_METADATA, any()) }
        assertEquals(EXPECTED_LOG_EVENT_WITH_METADATA_ACTION, viewModel.uiState.value.lastAction)
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
