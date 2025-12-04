package com.cramsan.edifikana.client.lib.features.home.propertiesoverview

import app.cash.turbine.test
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.resources.StringProvider
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
import kotlin.test.assertNull
import kotlinx.coroutines.launch

/**
 * It is recommended to use the [CoroutineTest] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runCoroutineTest` function to run your tests.
 *
 * @see CoroutineTest
 */
// TODO: Move this file to the respective folder in the test folder.
@Suppress("UNCHECKED_CAST")
class PropertiesOverviewViewModelTest : CoroutineTest() {

    private lateinit var viewModel: PropertiesOverviewViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var windowEventBus: EventBus<WindowEvent>

    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    private lateinit var stringProvider: StringProvider

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        stringProvider = mockk()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )

        viewModel = PropertiesOverviewViewModel(
            dependencies = dependencies,
        )
    }

    @Test
    fun `test ui state`() = runCoroutineTest {
        assertNull(viewModel.uiState.value.title)
    }

    @Test
    fun `test events`() = runCoroutineTest {
        // Set up
        val verificationJob = launch {
            windowEventBus.events.test {
                // TODO: Update this test with the event that applies
                assertEquals(WindowEvent.NavigateBack, awaitItem())
                advanceUntilIdleAndAwaitComplete(this)
            }
        }

        // Act
        viewModel.onBackSelected()

        // Assert
        verificationJob.join()
    }
}
