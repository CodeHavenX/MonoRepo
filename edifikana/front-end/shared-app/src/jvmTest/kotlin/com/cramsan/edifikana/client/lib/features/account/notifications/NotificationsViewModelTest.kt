package com.cramsan.edifikana.client.lib.features.account.notifications

import app.cash.turbine.turbineScope
import com.cramsan.framework.test.advanceUntilIdleAndAwaitComplete
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.NotificationManager
import com.cramsan.edifikana.client.lib.models.Notification
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
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
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Test the [NotificationsViewModel] class.
 */
@OptIn(ExperimentalTime::class)
class NotificationsViewModelTest : CoroutineTest() {

    private lateinit var viewModel: NotificationsViewModel
    private lateinit var authManager: AuthManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var exceptionHandler: CollectorCoroutineExceptionHandler
    private lateinit var applicationEventReceiver: EventBus<ApplicationEvent>
    private lateinit var windowEventBus: EventBus<WindowEvent>

    /**
     * Setup the test.
     */
    @BeforeEach
    fun setupTest() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        applicationEventReceiver = EventBus()
        windowEventBus = EventBus()
        exceptionHandler = CollectorCoroutineExceptionHandler()
        authManager = mockk(relaxed = true)
        notificationManager = mockk(relaxed = true)
        viewModel = NotificationsViewModel(
            dependencies = ViewModelDependencies(
                appScope = testCoroutineScope,
                dispatcherProvider = UnifiedDispatcherProvider(testCoroutineDispatcher),
                coroutineExceptionHandler = exceptionHandler,
                applicationEventReceiver = applicationEventReceiver,
                windowEventReceiver = windowEventBus,
            ),
            authManager = authManager,
            notificationManager = notificationManager,
        )
    }

    /**
     * Test the [NotificationsViewModel.onBackSelected] method emits NavigateBack event.
     */
    @Test
    fun `test onBackSelected emits NavigateBack event`() = runCoroutineTest {
        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.onBackSelected()

            // Assert
            assertEquals(EdifikanaWindowsEvent.NavigateBack, turbine.awaitItem())
            advanceUntilIdleAndAwaitComplete(turbine)
        }
    }

    @Test
    fun `test initial state`() = runCoroutineTest {
        assertEquals(true, viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.title)
        assertEquals(emptyList(), viewModel.uiState.value.notifications)
    }

    @Test
    fun `test initialize loads notifications`() = runCoroutineTest {
        val notifications = listOf(
            Notification(
                id = NotificationId("1"),
                type = NotificationType.INVITE,
                description = "You have been invited to Test Org",
                isRead = false,
                createdAt = Instant.fromEpochSeconds(0),
                readAt = null,
                inviteId = InviteId("invite1"),
            ),
        )

        coEvery { notificationManager.getNotifications() } returns Result.success(notifications)

        viewModel.initialize()

        assertEquals("Notifications", viewModel.uiState.value.title)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.notifications.size)

        val inviteNotification = viewModel.uiState.value.notifications[0]
        assertIs<InviteNotificationUIModel>(inviteNotification)
        assertEquals("You have been invited to Test Org", inviteNotification.description)
        assertEquals(InviteId("invite1"), inviteNotification.inviteId)
    }

    @Test
    fun `test initialize with failure shows error snackbar`() = runCoroutineTest {
        coEvery { notificationManager.getNotifications() } returns Result.failure(Exception("Network error"))

        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.initialize()

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.ShowSnackbar("Failed to load notifications: Network error"),
                turbine.awaitItem()
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(emptyList(), viewModel.uiState.value.notifications)
    }

    @Test
    fun `test acceptInvite calls authManager and reloads`() = runCoroutineTest {
        val inviteId = InviteId("invite1")

        coEvery { authManager.acceptInvite(inviteId) } returns Result.success(Unit)
        coEvery { notificationManager.getNotifications() } returns Result.success(emptyList())

        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.acceptInvite(inviteId)

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.ShowSnackbar("Invitation accepted successfully"),
                turbine.awaitItem()
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }

        coVerify { authManager.acceptInvite(inviteId) }
        coVerify(atLeast = 1) { notificationManager.getNotifications() }
    }

    @Test
    fun `test acceptInvite with failure shows error snackbar`() = runCoroutineTest {
        val inviteId = InviteId("invite1")

        coEvery { authManager.acceptInvite(inviteId) } returns Result.failure(Exception("Accept failed"))

        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.acceptInvite(inviteId)

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.ShowSnackbar("Failed to accept invitation: Accept failed"),
                turbine.awaitItem()
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test declineInvite calls authManager and reloads`() = runCoroutineTest {
        val inviteId = InviteId("invite1")

        coEvery { authManager.declineInvite(inviteId) } returns Result.success(Unit)
        coEvery { notificationManager.getNotifications() } returns Result.success(emptyList())

        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.declineInvite(inviteId)

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.ShowSnackbar("Invitation declined"),
                turbine.awaitItem()
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }

        coVerify { authManager.declineInvite(inviteId) }
    }

    @Test
    fun `test declineInvite with failure shows error snackbar`() = runCoroutineTest {
        val inviteId = InviteId("invite1")

        coEvery { authManager.declineInvite(inviteId) } returns Result.failure(Exception("Decline failed"))

        turbineScope {
            // Arrange
            val turbine = windowEventBus.events.testIn(backgroundScope)

            // Act
            viewModel.declineInvite(inviteId)

            // Assert
            assertEquals(
                EdifikanaWindowsEvent.ShowSnackbar("Failed to decline invitation: Decline failed"),
                turbine.awaitItem()
            )
            advanceUntilIdleAndAwaitComplete(turbine)
        }

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `test markAsRead calls notificationManager`() = runCoroutineTest {
        val notificationId = NotificationId("1")
        val notification = Notification(
            id = notificationId,
            type = NotificationType.SYSTEM,
            description = "System notification",
            isRead = true,
            createdAt = Instant.fromEpochSeconds(0),
            readAt = Instant.fromEpochSeconds(1),
            inviteId = null,
        )

        coEvery { notificationManager.markAsRead(notificationId) } returns Result.success(notification)
        coEvery { notificationManager.getNotifications() } returns Result.success(emptyList())

        viewModel.markAsRead(notificationId)

        coVerify { notificationManager.markAsRead(notificationId) }
    }

    @Test
    fun `test invite notification without inviteId is filtered out`() = runCoroutineTest {
        val notifications = listOf(
            Notification(
                id = NotificationId("1"),
                type = NotificationType.INVITE,
                description = "You have been invited",
                isRead = false,
                createdAt = Instant.fromEpochSeconds(0),
                readAt = null,
                inviteId = null, // Missing inviteId
            ),
        )

        coEvery { notificationManager.getNotifications() } returns Result.success(notifications)

        viewModel.initialize()

        assertEquals(0, viewModel.uiState.value.notifications.size)
        assertTrue(exceptionHandler.exceptions.isEmpty())
    }
}
