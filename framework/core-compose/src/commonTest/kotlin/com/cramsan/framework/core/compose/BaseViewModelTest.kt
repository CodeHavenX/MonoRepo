package com.cramsan.framework.core.compose

import app.cash.turbine.turbineScope
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test the [BaseViewModel] class.
 * You can use this class as an example for how to test your own view models.
 *
 * It is recommended to use the [CoroutineTest] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runBlockingTest` function to run your tests.
 *
 * @see CoroutineTest
 */
@Suppress("UNCHECKED_CAST")
class BaseViewModelTest : CoroutineTest() {

    private lateinit var viewModel: TestableViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    private lateinit var windowEventReceiver: EventBus<WindowEvent>

    @BeforeTest
    fun setupTest() {
        exceptionHandler = CollectorCoroutineExceptionHandler()
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        AssertUtil.setInstance(NoopAssertUtil())
        applicationEventReceiver = EventBus()
        windowEventReceiver = EventBus()
        viewModel = TestableViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventReceiver,
            )
        )
    }

    @Test
    fun `test updating the title`() = runCoroutineTest {
        viewModel.setTitle("Test")

        assertEquals("Test", viewModel.uiState.value.title)
    }

    @Test
    fun `test emitting some numbers`() = runCoroutineTest {
        turbineScope {
            // Arrange
            val turbine = viewModel.events.testIn(backgroundScope)

            // Act
            viewModel.emitNumbers()

            // Assert
            assertEquals(TestableEvent.EmitNumber(1), turbine.awaitItem())
            assertEquals(TestableEvent.EmitNumber(2), turbine.awaitItem())
            assertEquals(TestableEvent.EmitNumber(3), turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `test emitting an application event`() = runCoroutineTest {
        turbineScope {
            // Arrange
            val turbine = windowEventReceiver.events.testIn(backgroundScope)

            // Act
            viewModel.emitApplicationEvent()

            // Assert
            assertEquals(TestableApplicationEvent.Signal, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `test throwing an exception`() = runCoroutineTest {
        viewModel.throwError()

        assertEquals(1, exceptionHandler.exceptions.size)
    }
}
