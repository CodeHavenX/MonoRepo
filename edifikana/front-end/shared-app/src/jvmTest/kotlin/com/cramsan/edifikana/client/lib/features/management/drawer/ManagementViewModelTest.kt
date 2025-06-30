package com.cramsan.edifikana.client.lib.features.management.drawer

import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull

/**
 * It is recommended to use the [CoroutineTest] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runBlockingTest` function to run your tests.
 *
 * @see CoroutineTest
 */
@Suppress("UNCHECKED_CAST")
class ManagementViewModelTest : CoroutineTest() {

    private lateinit var viewModel: ManagementViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    private lateinit var windowEventBus: EventBus<WindowEvent>


    @BeforeTest
    fun setupTest() {
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = mockk()
        windowEventBus = mockk()
        EventLogger.setInstance(mockk(relaxed = true))
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )
        viewModel = ManagementViewModel(
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
        coEvery { windowEventBus.push(EdifikanaWindowsEvent.NavigateBack) } returns Unit

        // Act
        viewModel.onBackSelected()
        advanceUntilIdle()

        // Assert
        coVerify { windowEventBus.push(EdifikanaWindowsEvent.NavigateBack) }
    }
}