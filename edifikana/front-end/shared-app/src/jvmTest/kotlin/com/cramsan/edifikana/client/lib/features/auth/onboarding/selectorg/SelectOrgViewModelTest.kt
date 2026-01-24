package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import app.cash.turbine.test
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.NotificationManager
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CollectorCoroutineExceptionHandler
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.launch

/**
 * Unit tests for [SelectOrgViewModel].
 *
 * @see CoroutineTest
 */
class SelectOrgViewModelTest : CoroutineTest() {

    private lateinit var viewModel: SelectOrgViewModel

    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    private lateinit var windowEventBus: EventBus<WindowEvent>

    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>

    private lateinit var authManager: AuthManager

    private lateinit var notificationManager: NotificationManager

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        exceptionHandler = CollectorCoroutineExceptionHandler()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        authManager = mockk()
        notificationManager = mockk(relaxed = true)
        coEvery { authManager.signOut() } returns Result.success(Unit)
        val dependencies = ViewModelDependencies(
            appScope = testCoroutineScope,
            dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
            coroutineExceptionHandler = exceptionHandler,
            applicationEventReceiver = applicationEventReceiver,
            windowEventReceiver = windowEventBus,
        )

        viewModel = SelectOrgViewModel(
            authManager = authManager,
            notificationManager = notificationManager,
            dependencies = dependencies,
        )
    }

    @Test
    fun `test initial ui state`() = runCoroutineTest {
        assertEquals(SelectOrgUIState.Default, viewModel.uiState.value)
    }

    @Test
    fun `test createOrganization emits NavigateToScreen event`() = runCoroutineTest {
        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToScreen(AuthDestination.CreateNewOrgDestination),
                    awaitItem()
                )
            }
        }
        viewModel.createOrganization()

        // Assert
        verificationJob.join()
    }

    @Test
    fun `test requestSignOut does not throw`() = runCoroutineTest {
        // Act - should not throw
        viewModel.requestSignOut()
    }

    @Test
    fun `test confirmSignOut calls signOut and emits NavigateToNavGraph event`() = runCoroutineTest {
        // Act
        val verificationJob = launch {
            windowEventBus.events.test {
                assertEquals(
                    EdifikanaWindowsEvent.NavigateToNavGraph(
                        EdifikanaNavGraphDestination.AuthNavGraphDestination,
                        clearStack = true,
                    ),
                    awaitItem()
                )
            }
        }
        viewModel.confirmSignOut()

        // Assert
        coVerify { authManager.signOut() }
        verificationJob.join()
    }
}
