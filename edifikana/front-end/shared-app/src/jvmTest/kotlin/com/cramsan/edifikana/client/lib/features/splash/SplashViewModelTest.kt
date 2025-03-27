package com.cramsan.edifikana.client.lib.features.splash

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.ActivityDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import com.cramsan.framework.test.applyNoopFrameworkSingletons
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

/**
 * It is recommended to use the [TestBase] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runBlockingTest` function to run your tests.
 *
 * @see TestBase
 * TODO: Move this file to the respective folder in the test folder.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class SplashViewModelTest : TestBase() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var authManager: AuthManager
    private lateinit var propertyManager: PropertyManager

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    @BeforeTest
    fun setupTest() {
        // Apply the Noop framework singletons to avoid side effects
        applyNoopFrameworkSingletons()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
        )
        authManager = mockk()
        propertyManager = mockk()
        viewModel = SplashViewModel(
            dependencies = dependencies,
            authManager = authManager,
            propertyManager = propertyManager,
        )
    }

    @Test
    fun `test onBackSelected emits NavigateBack event`() = runBlockingTest {
        val verificationJob = launch {
            viewModel.events.test {
                assertEquals(SplashEvent.TriggerApplicationEvent(EdifikanaApplicationEvent.NavigateBack), awaitItem())
                advanceUntilIdleAndAwaitComplete(this)
            }
        }

        viewModel.onBackSelected()

        verificationJob.join()
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }

    @Test
    fun `test enforceAuth when not signed in`() = runBlockingTest {
        coEvery { authManager.isSignedIn() } returns Result.success(false)
        coEvery { propertyManager.setActiveProperty(null) } returns  Result.success(Unit)

        val verificationJob = launch {
            viewModel.events.test {
                assertEquals(
                    SplashEvent.TriggerApplicationEvent(
                        EdifikanaApplicationEvent.NavigateToActivity(
                            ActivityDestination.AuthDestination,
                            clearStack = true,
                        )
                    ), awaitItem()
                )
                advanceUntilIdleAndAwaitComplete(this)
            }
        }

        viewModel.enforceAuth()

        verificationJob.join()
        assertTrue(exceptionHandler.exceptions.isEmpty())
        coVerify { propertyManager.setActiveProperty(null) }
    }

    @Test
    fun `test enforceAuth when signed in`() = runBlockingTest {
        coEvery { authManager.isSignedIn() } returns Result.success(true)
        coEvery { propertyManager.getPropertyList() } returns Result.success(emptyList())
        coEvery { propertyManager.setActiveProperty(null) } returns  Result.success(Unit)

        val verificationJob = launch {
            viewModel.events.test {
                assertEquals(
                    SplashEvent.TriggerApplicationEvent(
                        EdifikanaApplicationEvent.NavigateToActivity(ActivityDestination.MainDestination)
                    ), awaitItem()
                )
                advanceUntilIdleAndAwaitComplete(this)
            }
        }

        viewModel.enforceAuth()

        verificationJob.join()
        assertTrue(exceptionHandler.exceptions.isEmpty())
        coVerify { propertyManager.setActiveProperty(null) }
    }
}