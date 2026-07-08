package com.cramsan.edifikana.client.lib.features.window

import app.cash.turbine.turbineScope
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test the [EdifikanaWindowViewModel] class.
 */
class EdifikanaWindowViewModelTest : CoroutineTest() {

    private lateinit var windowEventEmitter: EventBus<WindowEvent>
    private lateinit var delegatedEvents: EventBus<EdifikanaWindowDelegatedEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var viewModel: EdifikanaWindowViewModel

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        windowEventEmitter = EventBus()
        delegatedEvents = EventBus()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        viewModel = EdifikanaWindowViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            windowEventEmitter = windowEventEmitter,
            delegatedEvents = delegatedEvents,
        )
    }

    /**
     * Test [EdifikanaWindowViewModel.handleDeepLink] navigates straight to the resolved
     * destination, clearing the top of the back stack.
     */
    @Test
    fun `test handleDeepLink navigates to the resolved destination with clearTop`() = runCoroutineTest {
        // Arrange
        val destination = mockk<Destination>()

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.handleDeepLink(destination)

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(destination, clearTop = true),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
