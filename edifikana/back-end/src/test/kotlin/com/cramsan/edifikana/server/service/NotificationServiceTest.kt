package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.NotificationDatastore
import com.cramsan.edifikana.server.datastore.UserDatastore
import com.cramsan.edifikana.server.service.models.Notification
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource

/**
 * Test class for [NotificationService].
 */
@OptIn(ExperimentalTime::class)
class NotificationServiceTest {
    private lateinit var notificationDatastore: NotificationDatastore
    private lateinit var userDatastore: UserDatastore
    private lateinit var notificationService: NotificationService
    private lateinit var testTimeSource: TestTimeSource

    /**
     * Sets up the test environment by initializing mocks.
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        notificationDatastore = mockk()
        userDatastore = mockk()
        testTimeSource = TestTimeSource()
        notificationService = NotificationService(
            notificationDatastore,
        )
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that getNotificationsForUser retrieves notifications from the datastore.
     */
    @Test
    fun `getNotificationsForUser should return notifications from datastore`() = runTest {
        // Arrange
        val userId = UserId("user123")
        val notifications = listOf(mockk<Notification>(), mockk<Notification>())
        coEvery {
            notificationDatastore.getNotificationsForUser(
                userId,
                false,
                null,
            )
        } returns Result.success(notifications)

        // Act
        val result = notificationService.getNotificationsForUser(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(notifications, result.getOrThrow())
        coVerify { notificationDatastore.getNotificationsForUser(userId, false, null) }
    }

    /**
     * Tests that getNotificationsForUser with unreadOnly filter works.
     */
    @Test
    fun `getNotificationsForUser should filter unread only when requested`() = runTest {
        // Arrange
        val userId = UserId("user123")
        val notifications = listOf(mockk<Notification>())
        coEvery {
            notificationDatastore.getNotificationsForUser(
                userId,
                true,
                10,
            )
        } returns Result.success(notifications)

        // Act
        val result = notificationService.getNotificationsForUser(userId, unreadOnly = true, limit = 10)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(notifications, result.getOrThrow())
        coVerify { notificationDatastore.getNotificationsForUser(userId, true, 10) }
    }

    /**
     * Tests that getNotification retrieves a single notification by ID.
     */
    @Test
    fun `getNotification should return notification from datastore`() = runTest {
        // Arrange
        val notificationId = NotificationId("notif123")
        val notification = mockk<Notification>()
        coEvery { notificationDatastore.getNotification(notificationId) } returns Result.success(notification)

        // Act
        val result = notificationService.getNotification(notificationId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(notification, result.getOrThrow())
        coVerify { notificationDatastore.getNotification(notificationId) }
    }

    /**
     * Tests that getNotification returns null when notification is not found.
     */
    @Test
    fun `getNotification should return null when not found`() = runTest {
        // Arrange
        val notificationId = NotificationId("notif123")
        coEvery { notificationDatastore.getNotification(notificationId) } returns Result.success(null)

        // Act
        val result = notificationService.getNotification(notificationId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrThrow())
        coVerify { notificationDatastore.getNotification(notificationId) }
    }

    /**
     * Tests that markAsRead marks a notification as read.
     */
    @Test
    fun `markAsRead should mark notification as read`() = runTest {
        // Arrange
        val notificationId = NotificationId("notif123")
        val notification = mockk<Notification>()
        coEvery { notificationDatastore.markAsRead(notificationId) } returns Result.success(notification)

        // Act
        val result = notificationService.markAsRead(notificationId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(notification, result.getOrThrow())
        coVerify { notificationDatastore.markAsRead(notificationId) }
    }

    /**
     * Tests that markAsRead returns failure when datastore fails.
     */
    @Test
    fun `markAsRead should return failure when datastore fails`() = runTest {
        // Arrange
        val notificationId = NotificationId("notif123")
        val error = Exception("Notification not found")
        coEvery { notificationDatastore.markAsRead(notificationId) } returns Result.failure(error)

        // Act
        val result = notificationService.markAsRead(notificationId)

        // Assert
        assertTrue(result.isFailure)
        coVerify { notificationDatastore.markAsRead(notificationId) }
    }

    /**
     * Tests that deleteNotification deletes a notification.
     */
    @Test
    fun `deleteNotification should delete notification`() = runTest {
        // Arrange
        val notificationId = NotificationId("notif123")
        coEvery { notificationDatastore.deleteNotification(notificationId) } returns Result.success(true)

        // Act
        val result = notificationService.deleteNotification(notificationId)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
        coVerify { notificationDatastore.deleteNotification(notificationId) }
    }

    /**
     * Tests that deleteNotification returns false when notification not found.
     */
    @Test
    fun `deleteNotification should return false when not found`() = runTest {
        // Arrange
        val notificationId = NotificationId("notif123")
        coEvery { notificationDatastore.deleteNotification(notificationId) } returns Result.success(false)

        // Act
        val result = notificationService.deleteNotification(notificationId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(false, result.getOrThrow())
        coVerify { notificationDatastore.deleteNotification(notificationId) }
    }

    /**
     * Tests that linkNotificationsToUser links notifications to a user.
     */
    @Test
    fun `linkNotificationsToUser should link notifications to user`() = runTest {
        // Arrange
        val email = "test@example.com"
        val userId = UserId("user123")
        coEvery { notificationDatastore.linkNotificationsToUser(email, userId) } returns Result.success(3)

        // Act
        val result = notificationService.linkNotificationsToUser(email, userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrThrow())
        coVerify { notificationDatastore.linkNotificationsToUser(email, userId) }
    }

    /**
     * Tests that linkNotificationsToUser returns 0 when no notifications found.
     */
    @Test
    fun `linkNotificationsToUser should return 0 when no notifications found`() = runTest {
        // Arrange
        val email = "test@example.com"
        val userId = UserId("user123")
        coEvery { notificationDatastore.linkNotificationsToUser(email, userId) } returns Result.success(0)

        // Act
        val result = notificationService.linkNotificationsToUser(email, userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow())
        coVerify { notificationDatastore.linkNotificationsToUser(email, userId) }
    }
}
