package com.cramsan.framework.core.compose

import app.cash.turbine.test
import com.cramsan.framework.core.CollectorCoroutineExceptionHandler
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import com.cramsan.framework.test.applyNoopFrameworkSingletons
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.yield

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

    @BeforeTest
    fun setupTest() {
        // Apply the Noop framework singletons to avoid side effects
        applyNoopFrameworkSingletons()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        viewModel = TestableViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
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
    fun `test throwing an exception`() = runBlockingTest {
        viewModel.throwError()

        assertEquals(1, exceptionHandler.exceptions.size)
    }
}
