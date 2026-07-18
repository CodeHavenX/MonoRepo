package com.cramsan.templatereplaceme.client.lib.features.window

import androidx.compose.material3.SnackbarResult
import app.cash.turbine.turbineScope
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


/**
 * Tests for [TemplateReplaceMeWindowViewModel].
 */
class TemplateReplaceMeWindowViewModelTest : CoroutineTest() {

    private lateinit var viewModel: TemplateReplaceMeWindowViewModel
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var delegatedEventsBus: EventBus<TemplateReplaceMeWindowDelegatedEvent>
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        delegatedEventsBus = EventBus()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        viewModel = TemplateReplaceMeWindowViewModel(
            dependencies = dependencies,
            windowEventEmitter = windowEventBus,
            delegatedEvents = delegatedEventsBus,
        )
    }

    @Test
    fun `handleSnackbarResult ActionPerformed pushes delegated event`(): Unit = runCoroutineTest {
        turbineScope {
            val turbine = delegatedEventsBus.events.testIn(backgroundScope)

            viewModel.handleSnackbarResult(SnackbarResult.ActionPerformed)

            val event = turbine.awaitItem()
            assertTrue(event is TemplateReplaceMeWindowDelegatedEvent.HandleSnackbarResult)
            assertEquals(SnackbarResult.ActionPerformed, event.result)
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
