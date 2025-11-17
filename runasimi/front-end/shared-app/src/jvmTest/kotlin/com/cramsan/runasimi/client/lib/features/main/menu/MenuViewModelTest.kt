package com.cramsan.runasimi.client.lib.features.main.menu

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
import com.cramsan.runasimi.client.lib.features.main.menu.MenuViewModel
import com.cramsan.runasimi.client.lib.features.window.RunasimiWindowsEvent
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * It is recommended to use the [com.cramsan.framework.test.CoroutineTest] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runCoroutineTest` function to run your tests.
 *
 * @see com.cramsan.framework.test.CoroutineTest
 */
@Suppress("UNCHECKED_CAST")
class MenuViewModelTest : CoroutineTest() {

    private lateinit var viewModel: MenuViewModel

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

        viewModel = MenuViewModel(
            dependencies = dependencies,
        )
    }

    @Test
    fun `test initial ui state`() = runCoroutineTest {
        assertEquals(SelectableDrawerItem.Numbers, viewModel.uiState.value.selectedItem)
    }

    @Test
    fun `test events`() = runCoroutineTest {
        // Set up
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(RunasimiWindowsEvent.NavigateBack, awaitItem())
                advanceUntilIdleAndAwaitComplete(this)
            }
        }

        // Act
        viewModel.onBackSelected()

        // Assert
        verificationJob.join()
    }
}