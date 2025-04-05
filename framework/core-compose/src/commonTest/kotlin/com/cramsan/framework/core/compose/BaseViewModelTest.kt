package com.cramsan.framework.core.compose

import app.cash.turbine.test
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * Test the [BaseViewModel] class.
 * You can use this class as an example for how to test your own view models.
 *
 * It is recommended to use the [TestBase] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runBlockingTest` function to run your tests.
 *
 * @see TestBase
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class BaseViewModelTest : TestBase() {

    private lateinit var viewModel: TestableViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver

    @BeforeTest
    fun setupTest() {
        exceptionHandler = CollectorCoroutineExceptionHandler()
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        AssertUtil.setInstance(NoopAssertUtil())
        applicationEventReceiver = SharedFlowApplicationReceiver()
        viewModel = TestableViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
            )
        )
    }

    @Test
    fun `test updating the title`() = runBlockingTest {
        viewModel.setTitle("Test")

        assertEquals("Test", viewModel.uiState.value.title)
    }

    @Test
    fun `test emitting some numbers`() = runBlockingTest {
        val verificationJob = launch {
            viewModel.events.test {
                assertEquals(TestableEvent.EmitNumber(1), awaitItem())
                assertEquals(TestableEvent.EmitNumber(2), awaitItem())
                assertEquals(TestableEvent.EmitNumber(3), awaitItem())
                advanceUntilIdleAndAwaitComplete(this)
            }
        }

        viewModel.emitNumbers()

        verificationJob.join()
    }

    @Test
    fun `test emitting an application event`() = runBlockingTest {
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(TestableApplicationEvent.Signal, awaitItem())
                advanceUntilIdleAndAwaitComplete(this)
            }
        }

        viewModel.emitApplicationEvent()

        verificationJob.join()
    }

    @Test
    fun `test throwing an exception`() = runBlockingTest {
        viewModel.throwError()

        assertEquals(1, exceptionHandler.exceptions.size)
    }
}
