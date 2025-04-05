package ${PACKAGE_NAME}

import com.cramsan.framework.core.CollectorCoroutineExceptionHandler
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.test.applyNoopFrameworkSingletons
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * It is recommended to use the [TestBase] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runBlockingTest` function to run your tests.
 *
 * @see TestBase
 */
 // TODO: Move this file to the respective folder in the test folder.
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class ${NAME}ViewModelTest : TestBase() {

    private lateinit var viewModel: ${NAME}ViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver

    @BeforeTest
    fun setupTest() {
        // Apply the Noop framework singletons to avoid side effects
        applyNoopFrameworkSingletons()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = SharedFlowApplicationReceiver()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
        )
        viewModel = ${NAME}ViewModel(
            dependencies = dependencies,
        )
    }

    @Test
    fun `test ui state`() = runBlockingTest {
        assertNull(viewModel.uiState.value.title)
    }
    
        @Test
    fun `test events`() = runBlockingTest {
        // Set up
        val verificationJob = launch {
            applicationEventReceiver.events.test {
                assertEquals(ApplicationEvent.NavigateBack, awaitItem())
                advanceUntilIdleAndAwaitComplete(this)
            }
        }

        // Act
        viewModel.onBackSelected()
        
        // Assert
        verificationJob.join()
    }
}
