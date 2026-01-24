package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.Notification
import com.cramsan.edifikana.client.lib.service.NotificationService
import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Unit tests for the NotificationManager class.
 */
@OptIn(ExperimentalTime::class)
class NotificationManagerTest : CoroutineTest() {

    private lateinit var dependencies: ManagerDependencies
    private lateinit var notificationService: NotificationService
    private lateinit var manager: NotificationManager

    /**
     * Sets up the test environment, initializing mocks and the NotificationManager instance.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        notificationService = mockk(relaxed = true)

        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        manager = NotificationManager(dependencies, notificationService)
    }

    /**
     * Tests that getNotifications returns the list from service.
     */
    @Test
    fun `getNotifications returns list from service`() = runTest {
        // Arrange
        val notifications = listOf(
            Notification(
                id = NotificationId("1"),
                organizationId = OrganizationId("org-1"),
                type = NotificationType.INVITE,
                isRead = false,
                createdAt = Instant.fromEpochSeconds(0),
                readAt = null,
                inviteId = null,
            )
        )
        coEvery { notificationService.getNotifications() } returns Result.success(notifications)

        // Act
        val result = manager.getNotifications()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(notifications, result.getOrNull())
        coVerify { notificationService.getNotifications() }
    }

    /**
     * Tests that getNotifications returns failure when service fails.
     */
    @Test
    fun `getNotifications returns failure when service errors`() = runTest {
        // Arrange
        val exception = Exception("network error")
        coEvery { notificationService.getNotifications() } returns Result.failure(exception)

        // Act
        val result = manager.getNotifications()

        // Assert
        assertTrue(result.isFailure)
        coVerify { notificationService.getNotifications() }
    }

    /**
     * Tests that getNotification returns a notification from service.
     */
    @Test
    fun `getNotification returns notification from service`() = runTest {
        // Arrange
        val notificationId = NotificationId("1")
        val notification = Notification(
            id = notificationId,
            organizationId = OrganizationId("org-1"),
            type = NotificationType.SYSTEM,
            isRead = false,
            createdAt = Instant.fromEpochSeconds(0),
            readAt = null,
            inviteId = null,
        )
        coEvery { notificationService.getNotification(notificationId) } returns Result.success(notification)

        // Act
        val result = manager.getNotification(notificationId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(notification, result.getOrNull())
        coVerify { notificationService.getNotification(notificationId) }
    }

    /**
     * Tests that markAsRead returns updated notification from service.
     */
    @Test
    fun `markAsRead returns updated notification from service`() = runTest {
        // Arrange
        val notificationId = NotificationId("1")
        val notification = Notification(
            id = notificationId,
            organizationId = OrganizationId("org-1"),
            type = NotificationType.SYSTEM,
            isRead = true,
            createdAt = Instant.fromEpochSeconds(0),
            readAt = Instant.fromEpochSeconds(1),
            inviteId = null,
        )
        coEvery { notificationService.markAsRead(notificationId) } returns Result.success(notification)

        // Act
        val result = manager.markAsRead(notificationId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(notification, result.getOrNull())
        assertEquals(true, result.getOrNull()?.isRead)
        coVerify { notificationService.markAsRead(notificationId) }
    }

    /**
     * Tests that markAsRead returns failure when service fails.
     */
    @Test
    fun `markAsRead returns failure when service errors`() = runTest {
        // Arrange
        val notificationId = NotificationId("1")
        val exception = Exception("mark as read failed")
        coEvery { notificationService.markAsRead(notificationId) } returns Result.failure(exception)

        // Act
        val result = manager.markAsRead(notificationId)

        // Assert
        assertTrue(result.isFailure)
        coVerify { notificationService.markAsRead(notificationId) }
    }

    /**
     * Tests that deleteNotification calls service and returns success.
     */
    @Test
    fun `deleteNotification calls service and returns success`() = runTest {
        // Arrange
        val notificationId = NotificationId("1")
        coEvery { notificationService.deleteNotification(notificationId) } returns Result.success(Unit)

        // Act
        val result = manager.deleteNotification(notificationId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { notificationService.deleteNotification(notificationId) }
    }

    /**
     * Tests that deleteNotification returns failure when service fails.
     */
    @Test
    fun `deleteNotification returns failure when service errors`() = runTest {
        // Arrange
        val notificationId = NotificationId("1")
        val exception = Exception("delete failed")
        coEvery { notificationService.deleteNotification(notificationId) } returns Result.failure(exception)

        // Act
        val result = manager.deleteNotification(notificationId)

        // Assert
        assertTrue(result.isFailure)
        coVerify { notificationService.deleteNotification(notificationId) }
    }
}
