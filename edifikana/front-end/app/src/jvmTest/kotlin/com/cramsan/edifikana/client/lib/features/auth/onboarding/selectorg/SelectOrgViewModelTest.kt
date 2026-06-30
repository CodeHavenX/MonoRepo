package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import app.cash.turbine.turbineScope
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
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
import com.cramsan.edifikana.lib.model.invite.InviteId
import kotlin.test.assertEquals
import kotlin.test.assertIs

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
        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.createOrganization()

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(AuthDestination.CreateNewOrgDestination),
                turbine.awaitItem()
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `requestSignOut sets dialog to ConfirmSignOut`() = runCoroutineTest {
        viewModel.requestSignOut()

        assertIs<SelectOrgDialogState.ConfirmSignOut>(viewModel.uiState.value.dialog)
    }

    @Test
    fun `requestJoinOrganization sets dialog to ConfirmJoinOrg`() = runCoroutineTest {
        val inviteId = InviteId("invite-1")

        viewModel.requestJoinOrganization(inviteId)

        val dialog = assertIs<SelectOrgDialogState.ConfirmJoinOrg>(viewModel.uiState.value.dialog)
        assertEquals(inviteId, dialog.inviteId)
    }

    @Test
    fun `dismissDialog sets dialog to None`() = runCoroutineTest {
        viewModel.requestSignOut()

        viewModel.dismissDialog()

        assertIs<SelectOrgDialogState.None>(viewModel.uiState.value.dialog)
    }

    @Test
    fun `test confirmSignOut calls signOut and emits NavigateToNavGraph event`() = runCoroutineTest {
        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.confirmSignOut()

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.AuthNavGraphDestination,
                    clearStack = true,
                ),
                turbine.awaitItem()
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }

        // Assert
        coVerify { authManager.signOut() }
    }
}
