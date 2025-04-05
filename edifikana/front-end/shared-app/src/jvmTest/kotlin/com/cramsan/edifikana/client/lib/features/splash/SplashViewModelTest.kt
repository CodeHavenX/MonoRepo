package com.cramsan.edifikana.client.lib.features.splash

import com.cramsan.edifikana.client.lib.features.ActivityRouteDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEventReceiver
import com.cramsan.framework.core.compose.SharedFlowApplicationReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * It is recommended to use the [TestBase] class to run your tests. To run your tests annotate your functions with
 * `@Test` and use the `runBlockingTest` function to run your tests.
 *
 * @see TestBase
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
class SplashViewModelTest : TestBase() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var authManager: AuthManager
    private lateinit var propertyManager: PropertyManager

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var applicationEventReceiver: SharedFlowApplicationReceiver

    @BeforeTest
    fun setupTest() {
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = mockk(relaxed = true)
        EventLogger.setInstance(mockk(relaxed = true))
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
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
        viewModel.onBackSelected()

        assertTrue(exceptionHandler.exceptions.isEmpty())
        coVerify { applicationEventReceiver.receiveApplicationEvent(EdifikanaApplicationEvent.NavigateBack) }
    }

    @Test
    fun `test enforceAuth when not signed in`() = runBlockingTest {
        coEvery { authManager.isSignedIn() } returns Result.success(false)
        coEvery { propertyManager.setActiveProperty(null) } returns  Result.success(Unit)

        viewModel.enforceAuth()

        assertTrue(exceptionHandler.exceptions.isEmpty())
        coVerify { propertyManager.setActiveProperty(null) }
        coVerify { applicationEventReceiver.receiveApplicationEvent(
            EdifikanaApplicationEvent.NavigateToActivity(
                ActivityRouteDestination.AuthRouteDestination,
                clearStack = true,
                )
            )
        }
    }

    @Test
    fun `test enforceAuth when signed in`() = runBlockingTest {
        coEvery { authManager.isSignedIn() } returns Result.success(true)
        coEvery { propertyManager.getPropertyList() } returns Result.success(emptyList())
        coEvery { propertyManager.setActiveProperty(null) } returns  Result.success(Unit)

        viewModel.enforceAuth()

        assertTrue(exceptionHandler.exceptions.isEmpty())
        coVerify { propertyManager.setActiveProperty(null) }
        coVerify {
            applicationEventReceiver.receiveApplicationEvent(
                EdifikanaApplicationEvent.NavigateToActivity(
                    ActivityRouteDestination.ManagementRouteDestination
                )
            )
        }
    }
}