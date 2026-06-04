package com.cramsan.templatereplaceme.client.lib.features.splash

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
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowNavGraphDestination
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowsEvent
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [SplashViewModel].
 */
class SplashViewModelTest : CoroutineTest() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        viewModel = SplashViewModel(dependencies = dependencies)
    }

    @Test
    fun `initial ui state has isLoading true`() = runCoroutineTest {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `navigateToMainScreen emits NavigateToNavGraph to AuthNavGraphDestination`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            viewModel.navigateToMainScreen()

            val event = turbine.awaitItem()
            assertTrue(event is TemplateReplaceMeWindowsEvent.NavigateToNavGraph)
            assertEquals(
                TemplateReplaceMeWindowNavGraphDestination.AuthNavGraphDestination,
                (event as TemplateReplaceMeWindowsEvent.NavigateToNavGraph).destination,
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
