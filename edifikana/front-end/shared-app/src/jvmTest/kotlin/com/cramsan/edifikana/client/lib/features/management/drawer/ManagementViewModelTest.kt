package com.cramsan.edifikana.client.lib.features.management.drawer

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEventReceiver
import com.cramsan.framework.core.compose.SharedFlowApplicationReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * It is recommended to use the [TestBase] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runBlockingTest` function to run your tests.
 *
 * @see TestBase
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class ManagementViewModelTest : TestBase() {

    private lateinit var viewModel: ManagementViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver

    @BeforeTest
    fun setupTest() {
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = mockk()
        EventLogger.setInstance(mockk(relaxed = true))
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
        )
        viewModel = ManagementViewModel(
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

        // Act
        viewModel.onBackSelected()

        // Assert
        coVerify { applicationEventReceiver.receiveApplicationEvent(EdifikanaApplicationEvent.NavigateBack) }
    }
}