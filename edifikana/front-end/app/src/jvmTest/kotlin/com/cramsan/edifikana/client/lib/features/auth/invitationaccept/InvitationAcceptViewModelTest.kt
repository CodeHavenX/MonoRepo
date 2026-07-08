package com.cramsan.edifikana.client.lib.features.auth.invitationaccept

import app.cash.turbine.turbineScope
import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.NotificationManager
import com.cramsan.edifikana.client.lib.models.Notification
import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.notification.NotificationId
import com.cramsan.edifikana.lib.model.notification.NotificationType
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
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import edifikana_lib.Res
import edifikana_lib.error_message_unexpected_error
import edifikana_lib.invitation_accept_screen_error_invalid_invite
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

private val INVITE_ID = InviteId("invite-123")

/**
 * Test the [InvitationAcceptViewModel] class.
 */
class InvitationAcceptViewModelTest : CoroutineTest() {

    private lateinit var authManager: AuthManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var stringProvider: StringProvider
    private lateinit var viewModel: InvitationAcceptViewModel
    private lateinit var windowEventBus: EventBus<WindowEvent>
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler

    @BeforeTest
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authManager = mockk(relaxed = true)
        notificationManager = mockk()
        stringProvider = mockk()
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        viewModel = InvitationAcceptViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                windowEventReceiver = windowEventBus,
                applicationEventReceiver = applicationEventReceiver,
            ),
            authManager = authManager,
            notificationManager = notificationManager,
            stringProvider = stringProvider,
        )
    }

    private fun notification(inviteId: InviteId?, type: NotificationType, description: String) = Notification(
        id = NotificationId("notification-id"),
        type = type,
        description = description,
        isRead = false,
        createdAt = Instant.DISTANT_PAST,
        readAt = null,
        inviteId = inviteId,
    )

    /**
     * Test [InvitationAcceptViewModel.loadInvitation] when signed out does not fetch notifications
     * and leaves invitationSummary null.
     */
    @Test
    fun `test loadInvitation when signed out does not fetch notifications`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.isSignedIn() } returns Result.success(false)

        // Act
        viewModel.loadInvitation(INVITE_ID)

        // Assert
        assertFalse(viewModel.uiState.value.isUserSignedIn)
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.invitationSummary)
        assertFalse(viewModel.uiState.value.isLoading)
        coVerify(exactly = 0) { notificationManager.getNotifications() }
    }

    /**
     * Test [InvitationAcceptViewModel.loadInvitation] with a blank invite id sets an error and
     * never checks session state.
     */
    @Test
    fun `test loadInvitation with blank inviteId sets error`() = runCoroutineTest {
        // Arrange
        val message = "This invitation link is invalid or has expired."
        coEvery {
            stringProvider.getString(Res.string.invitation_accept_screen_error_invalid_invite)
        } returns message

        // Act
        viewModel.loadInvitation(InviteId(""))

        // Assert
        assertEquals(message, viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
        coVerify(exactly = 0) { authManager.isSignedIn() }
    }

    /**
     * Test [InvitationAcceptViewModel.loadInvitation] when signed in populates invitationSummary
     * from the matching invite notification.
     */
    @Test
    fun `test loadInvitation when signed in populates invitationSummary from matching notification`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.isSignedIn() } returns Result.success(true)
        coEvery { notificationManager.getNotifications() } returns Result.success(
            listOf(
                notification(inviteId = InviteId("other-invite"), type = NotificationType.INVITE, description = "Wrong invite"),
                notification(inviteId = INVITE_ID, type = NotificationType.INVITE, description = "You have been invited to join Acme Corp."),
            ),
        )

        // Act
        viewModel.loadInvitation(INVITE_ID)

        // Assert
        assertTrue(viewModel.uiState.value.isUserSignedIn)
        assertEquals("You have been invited to join Acme Corp.", viewModel.uiState.value.invitationSummary)
    }

    /**
     * Test [InvitationAcceptViewModel.loadInvitation] when signed in but no matching notification
     * exists leaves invitationSummary null without surfacing an error.
     */
    @Test
    fun `test loadInvitation when signed in with no matching notification leaves invitationSummary null`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.isSignedIn() } returns Result.success(true)
        coEvery { notificationManager.getNotifications() } returns Result.success(emptyList())

        // Act
        viewModel.loadInvitation(INVITE_ID)

        // Assert
        assertNull(viewModel.uiState.value.invitationSummary)
        assertNull(viewModel.uiState.value.error)
    }

    /**
     * Test [InvitationAcceptViewModel.acceptInvitation] on success navigates to the home nav
     * graph with the back stack cleared.
     */
    @Test
    fun `test acceptInvitation success navigates to home nav graph`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.acceptInvite(INVITE_ID) } returns Result.success(Unit)

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.acceptInvitation(INVITE_ID)

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.NavigateToNavGraph(
                    EdifikanaNavGraphDestination.HomeNavGraphDestination,
                    clearStack = true,
                ),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
        coVerify { authManager.acceptInvite(INVITE_ID) }
        assertFalse(viewModel.uiState.value.isLoading)
    }

    /**
     * Test [InvitationAcceptViewModel.acceptInvitation] on failure surfaces the exception message
     * as the error and does not navigate.
     */
    @Test
    fun `test acceptInvitation failure sets error message`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.acceptInvite(INVITE_ID) } returns
            Result.failure(Exception("This invite is not for your email address"))

        // Act
        viewModel.acceptInvitation(INVITE_ID)

        // Assert
        assertEquals("This invite is not for your email address", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    /**
     * Test [InvitationAcceptViewModel.acceptInvitation] on failure with no exception message
     * falls back to the generic unexpected-error string.
     */
    @Test
    fun `test acceptInvitation failure with no message uses fallback error string`() = runCoroutineTest {
        // Arrange
        val fallback = "There was an unexpected error."
        coEvery { authManager.acceptInvite(INVITE_ID) } returns Result.failure(Exception())
        coEvery { stringProvider.getString(Res.string.error_message_unexpected_error) } returns fallback

        // Act
        viewModel.acceptInvitation(INVITE_ID)

        // Assert
        assertEquals(fallback, viewModel.uiState.value.error)
    }

    /**
     * Test [InvitationAcceptViewModel.declineInvitation] on success navigates to sign-in with the
     * back stack cleared.
     */
    @Test
    fun `test declineInvitation success navigates to sign-in`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.declineInvite(INVITE_ID) } returns Result.success(Unit)

        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.declineInvitation(INVITE_ID)

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.SignInDestination(),
                    clearStack = true,
                ),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
        coVerify { authManager.declineInvite(INVITE_ID) }
    }

    /**
     * Test [InvitationAcceptViewModel.declineInvitation] on failure surfaces an error and does
     * not navigate.
     */
    @Test
    fun `test declineInvitation failure sets error message`() = runCoroutineTest {
        // Arrange
        coEvery { authManager.declineInvite(INVITE_ID) } returns Result.failure(Exception("Invite not found"))

        // Act
        viewModel.declineInvitation(INVITE_ID)

        // Assert
        assertEquals("Invite not found", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    /**
     * Test [InvitationAcceptViewModel.navigateToSignUp] navigates to the sign-up screen, carrying
     * the invite id as a navigation argument.
     */
    @Test
    fun `test navigateToSignUp navigates to sign-up with inviteId`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.navigateToSignUp(INVITE_ID)

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(
                    AuthDestination.SignUpDestination(userEmail = "", inviteId = INVITE_ID),
                ),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    /**
     * Test [InvitationAcceptViewModel.navigateToSignIn] navigates to the sign-in screen, carrying
     * the invite id as a navigation argument.
     */
    @Test
    fun `test navigateToSignIn navigates to sign-in with inviteId`() = runCoroutineTest {
        turbineScope {
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.navigateToSignIn(INVITE_ID)

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.NavigateToScreen(AuthDestination.SignInDestination(inviteId = INVITE_ID)),
                turbine.awaitItem(),
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }
}
