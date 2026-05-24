package com.cramsan.framework.sample.shared.features.main.metrics

import app.cash.turbine.turbineScope
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.metrics.MetricType
import com.cramsan.framework.metrics.MetricsInterface
import com.cramsan.framework.sample.shared.features.SampleWindowEvent
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MetricsViewModelTest : CoroutineTest() {

    companion object {
        private const val EXPECTED_INITIALIZE_ACTION = "initialize() called"
        private const val EXPECTED_RECORD_COUNT_ACTION = "record(COUNT, sample, sample_count_tag) called"
        private const val EXPECTED_RECORD_LATENCY_ACTION = "record(LATENCY, sample, sample_latency_tag, 250ms) called"
        private const val EXPECTED_RECORD_EVENT_ACTION = "record(EVENT, sample, sample_event_tag, metadata) called"
        private const val EXPECTED_RECORD_SUCCESS_ACTION = "record(SUCCESS, sample, sample_success_tag) called"
        private const val EXPECTED_RECORD_FAILURE_ACTION = "record(FAILURE, sample, sample_failure_tag) called"
    }

    private lateinit var metrics: MetricsInterface
    private lateinit var viewModel: MetricsViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventBus: EventBus<ApplicationEvent>

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        metrics = mockk()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventBus = EventBus()
        windowEventBus = EventBus()
        viewModel = MetricsViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventBus,
            ),
            metrics = metrics,
        )
    }

    @Test
    fun `initial UIState is correct`() = runCoroutineTest {
        assertEquals(MetricsUIState.Initial, viewModel.uiState.value)
        assertEquals(MetricsUIState.Initial.lastAction, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `initialize calls metrics initialize and updates lastAction`() = runCoroutineTest {
        justRun { metrics.initialize() }

        viewModel.initialize()

        verify { metrics.initialize() }
        assertEquals(EXPECTED_INITIALIZE_ACTION, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `recordCount calls metrics record with COUNT type`() = runCoroutineTest {
        justRun { metrics.record(MetricType.COUNT, any(), any(), any(), any(), any()) }

        viewModel.recordCount()

        verify { metrics.record(MetricType.COUNT, any(), any(), any(), any(), any()) }
        assertEquals(EXPECTED_RECORD_COUNT_ACTION, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `recordLatency calls metrics record with LATENCY type`() = runCoroutineTest {
        justRun { metrics.record(MetricType.LATENCY, any(), any(), any(), any(), any()) }

        viewModel.recordLatency()

        verify { metrics.record(MetricType.LATENCY, any(), any(), any(), any(), any()) }
        assertEquals(EXPECTED_RECORD_LATENCY_ACTION, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `recordEvent calls metrics record with EVENT type`() = runCoroutineTest {
        justRun { metrics.record(MetricType.EVENT, any(), any(), any(), any(), any()) }

        viewModel.recordEvent()

        verify { metrics.record(MetricType.EVENT, any(), any(), any(), any(), any()) }
        assertEquals(EXPECTED_RECORD_EVENT_ACTION, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `recordSuccess calls metrics record with SUCCESS type`() = runCoroutineTest {
        justRun { metrics.record(MetricType.SUCCESS, any(), any(), any(), any(), any()) }

        viewModel.recordSuccess()

        verify { metrics.record(MetricType.SUCCESS, any(), any(), any(), any(), any()) }
        assertEquals(EXPECTED_RECORD_SUCCESS_ACTION, viewModel.uiState.value.lastAction)
    }

    @Test
    fun `recordFailure calls metrics record with FAILURE type`() = runCoroutineTest {
        justRun { metrics.record(MetricType.FAILURE, any(), any(), any(), any(), any()) }

        viewModel.recordFailure()

        verify { metrics.record(MetricType.FAILURE, any(), any(), any(), any(), any()) }
        assertEquals(EXPECTED_RECORD_FAILURE_ACTION, viewModel.uiState.value.lastAction)
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
