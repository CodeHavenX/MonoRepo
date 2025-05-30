package com.cramsan.edifikana.client.lib.features.account.notifications

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.EdifikanaWindowsEvent
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEventBus
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test the [NotificationsViewModel] class.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest : TestBase() {

    private lateinit var viewModel: NotificationsViewModel
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: ApplicationEventBus
    private lateinit var windowEventBus: WindowEventBus

    /**
     * Setup the test.
     */
    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = ApplicationEventBus()
        windowEventBus = WindowEventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        viewModel = NotificationsViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            )
        )
    }

    /**
     * Test the [NotificationsViewModel.onBackSelected] method emits NavigateBack event.
     */
    @Test
    fun `test onBackSelected emits NavigateBack event`() = runBlockingTest {
        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateBack,
                    awaitItem()
                )
            }
        }
        viewModel.onBackSelected()

        // Assert
        verificationJob.join()
    }
}