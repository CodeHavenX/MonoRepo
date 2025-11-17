package com.cramsan.runasimi.client.lib.features.main.yupay

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
import com.cramsan.runasimi.client.lib.manager.QuechuaManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle

/**
 * It is recommended to use the [CoroutineTest] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runCoroutineTest` function to run your tests.
 *
 * @see CoroutineTest
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class YupayViewModelTest : CoroutineTest() {

    private lateinit var viewModel: YupayViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var windowEventBus: EventBus<WindowEvent>

    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    private lateinit var stringProvider: StringProvider

    private lateinit var quechuaManager: QuechuaManager

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        stringProvider = mockk()
        quechuaManager = mockk()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )

        viewModel = YupayViewModel(
            dependencies = dependencies,
            quechuaManager = quechuaManager,
        )
    }

    @Test
    fun `test ui state`() = runCoroutineTest {
        assertNull(viewModel.uiState.value.content)
    }

    @Test
    fun `test generate new number updates ui state`() = runCoroutineTest {
        // Arrange
        coEvery { quechuaManager.generateNumberTranslation(any()) } returns mockk()

        // Act
        viewModel.generateNewNumber()

        // Assert
        val newContent = viewModel.uiState.value.content
        assertNotNull(newContent)
        coVerify { quechuaManager.generateNumberTranslation(any()) }
    }
}
