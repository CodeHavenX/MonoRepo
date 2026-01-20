package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.NotificationId
import com.cramsan.edifikana.lib.model.NotificationType
import com.cramsan.framework.utils.uuid.UUID
import org.koin.test.inject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Integration tests for [SupabaseNotificationDatastore].
 */
@OptIn(ExperimentalTime::class)
class SupabaseNotificationDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private val clock: Clock by inject()

    private lateinit var testPrefix: String

    @BeforeTest
    fun setup() {
        testPrefix = UUID.random()
    }

    /**
     * Tests that createNotification creates a notification without a user ID.
     */
    @Test
    fun `createNotification should create notification without user ID`() = runCoroutineTest {
        // Arrange
        val email = "${testPrefix}_notify@test.com"
        val organizationId = createTestOrganization("org_$testPrefix", "")

        // Act
        val result = notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = email,
            organizationId = organizationId,
            notificationType = NotificationType.INVITE,
        ).registerNotificationForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val notification = result.getOrThrow()
        assertEquals(email, notification.recipientEmail)
        assertEquals(organizationId, notification.organizationId)
        assertEquals(NotificationType.INVITE, notification.notificationType)
        assertNull(notification.recipientUserId)
        assertFalse(notification.isRead)
        assertNull(notification.readAt)
    }

    /**
     * Tests that createNotification creates a notification with a user ID.
     */
    @Test
    fun `createNotification should create notification with user ID`() = runCoroutineTest {
        // Arrange
        val email = "${testPrefix}_notify@test.com"
        val userId = createTestUser("${testPrefix}_user@test.com")
        val organizationId = createTestOrganization("org_$testPrefix", "")

        // Act
        val result = notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = email,
            organizationId = organizationId,
            notificationType = NotificationType.SYSTEM,
        ).registerNotificationForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val notification = result.getOrThrow()
        assertEquals(email, notification.recipientEmail)
        assertEquals(userId, notification.recipientUserId)
        assertEquals(organizationId, notification.organizationId)
        assertEquals(NotificationType.SYSTEM, notification.notificationType)
        assertFalse(notification.isRead)
    }

    /**
     * Tests that getNotification returns the created notification.
     */
    @Test
    fun `getNotification should return created notification`() = runCoroutineTest {
        // Arrange
        val email = "${testPrefix}_notify@test.com"
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val createResult = notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = email,
            organizationId = organizationId,
            notificationType = NotificationType.INVITE,
        ).registerNotificationForDeletion()
        val created = createResult.getOrThrow()

        // Act
        val getResult = notificationDatastore.getNotification(created.notificationId)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrThrow()
        assertNotNull(fetched)
        assertEquals(created.notificationId, fetched.notificationId)
        assertEquals(email, fetched.recipientEmail)
        assertEquals(organizationId, fetched.organizationId)
    }

    /**
     * Tests that getNotification returns null for non-existent notification.
     */
    @Test
    fun `getNotification should return null for non-existent notification`() = runCoroutineTest {
        // Arrange
        val fakeId = NotificationId(testPrefix)

        // Act
        val result = notificationDatastore.getNotification(fakeId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    /**
     * Tests that getNotificationsForUser returns notifications for a user.
     */
    @Test
    fun `getNotificationsForUser should return notifications for user`() = runCoroutineTest {
        // Arrange
        val userId = createTestUser("${testPrefix}_user@test.com")
        val organizationId = createTestOrganization("org_$testPrefix", "")

        // Create two notifications for the user
        notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = "${testPrefix}_1@test.com",
            organizationId = organizationId,
            notificationType = NotificationType.INVITE,
        ).registerNotificationForDeletion()

        notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = "${testPrefix}_2@test.com",
            organizationId = organizationId,
            notificationType = NotificationType.SYSTEM,
        ).registerNotificationForDeletion()

        // Act
        val result = notificationDatastore.getNotificationsForUser(userId, unreadOnly = false, limit = null)

        // Assert
        assertTrue(result.isSuccess)
        val notifications = result.getOrThrow()
        assertEquals(2, notifications.size)
        assertTrue(notifications.all { it.recipientUserId == userId })
    }

    /**
     * Tests that getNotificationsForUser with unreadOnly returns only unread notifications.
     */
    @Test
    fun `getNotificationsForUser should filter unread only when requested`() = runCoroutineTest {
        // Arrange
        val userId = createTestUser("${testPrefix}_user@test.com")
        val organizationId = createTestOrganization("org_$testPrefix", "")

        // Create first notification and mark as read
        val first = notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = "${testPrefix}_1@test.com",
            organizationId = organizationId,
            notificationType = NotificationType.INVITE,
        ).registerNotificationForDeletion().getOrThrow()

        notificationDatastore.markAsRead(first.notificationId)

        // Create second notification (unread)
        notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = "${testPrefix}_2@test.com",
            organizationId = organizationId,
            notificationType = NotificationType.SYSTEM,
        ).registerNotificationForDeletion()

        // Act
        val result = notificationDatastore.getNotificationsForUser(userId, unreadOnly = true, limit = null)

        // Assert
        assertTrue(result.isSuccess)
        val notifications = result.getOrThrow()
        assertEquals(1, notifications.size)
        assertFalse(notifications.first().isRead)
    }

    /**
     * Tests that getNotificationsForUser respects limit.
     */
    @Test
    fun `getNotificationsForUser should respect limit parameter`() = runCoroutineTest {
        // Arrange
        val userId = createTestUser("${testPrefix}_user@test.com")
        val organizationId = createTestOrganization("org_$testPrefix", "")

        // Create three notifications
        repeat(3) { i ->
            notificationDatastore.createNotification(
                recipientUserId = userId,
                recipientEmail = "${testPrefix}_$i@test.com",
                organizationId = organizationId,
                notificationType = NotificationType.INVITE,
            ).registerNotificationForDeletion()
        }

        // Act
        val result = notificationDatastore.getNotificationsForUser(userId, unreadOnly = false, limit = 2)

        // Assert
        assertTrue(result.isSuccess)
        val notifications = result.getOrThrow()
        assertEquals(2, notifications.size)
    }

    /**
     * Tests that getNotificationsByEmail returns notifications for an email.
     */
    @Test
    fun `getNotificationsByEmail should return notifications for email`() = runCoroutineTest {
        // Arrange
        val email = "${testPrefix}_notify@test.com"
        val organizationId = createTestOrganization("org_$testPrefix", "")

        notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = email,
            organizationId = organizationId,
            notificationType = NotificationType.INVITE,
        ).registerNotificationForDeletion()

        // Act
        val result = notificationDatastore.getNotificationsByEmail(email)

        // Assert
        assertTrue(result.isSuccess)
        val notifications = result.getOrThrow()
        assertEquals(1, notifications.size)
        assertEquals(email, notifications.first().recipientEmail)
    }

    /**
     * Tests that getNotificationsByEmail returns empty list for non-existent email.
     */
    @Test
    fun `getNotificationsByEmail should return empty list for non-existent email`() = runCoroutineTest {
        // Arrange
        val email = "${testPrefix}_nonexistent@test.com"

        // Act
        val result = notificationDatastore.getNotificationsByEmail(email)

        // Assert
        assertTrue(result.isSuccess)
        val notifications = result.getOrThrow()
        assertTrue(notifications.isEmpty())
    }

    /**
     * Tests that markAsRead marks a notification as read.
     */
    @Test
    fun `markAsRead should mark notification as read`() = runCoroutineTest {
        // Arrange
        val email = "${testPrefix}_notify@test.com"
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val created = notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = email,
            organizationId = organizationId,
            notificationType = NotificationType.INVITE,
        ).registerNotificationForDeletion().getOrThrow()

        assertFalse(created.isRead)

        // Act
        val result = notificationDatastore.markAsRead(created.notificationId)

        // Assert
        assertTrue(result.isSuccess)
        val updated = result.getOrThrow()
        assertTrue(updated.isRead)
        assertNotNull(updated.readAt)
    }

    /**
     * Tests that markAsRead fails for non-existent notification.
     */
    @Test
    fun `markAsRead should fail for non-existent notification`() = runCoroutineTest {
        // Arrange
        val fakeId = NotificationId(testPrefix)

        // Act
        val result = notificationDatastore.markAsRead(fakeId)

        // Assert
        assertTrue(result.isFailure)
    }

    /**
     * Tests that deleteNotification deletes a notification.
     */
    @Test
    fun `deleteNotification should delete notification`() = runCoroutineTest {
        // Arrange
        val email = "${testPrefix}_notify@test.com"
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val created = notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = email,
            organizationId = organizationId,
            notificationType = NotificationType.INVITE,
        ).getOrThrow() // Not registering for deletion since we're deleting it

        // Act
        val deleteResult = notificationDatastore.deleteNotification(created.notificationId)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrThrow())

        // Verify it's gone
        val getResult = notificationDatastore.getNotification(created.notificationId)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrThrow())
    }

    /**
     * Tests that deleteNotification returns false for non-existent notification.
     */
    @Test
    fun `deleteNotification should return false for non-existent notification`() = runCoroutineTest {
        // Arrange
        val fakeId = NotificationId(testPrefix)

        // Act
        val result = notificationDatastore.deleteNotification(fakeId)

        // Assert
        assertTrue(result.isSuccess)
        assertFalse(result.getOrThrow())
    }

    /**
     * Tests that linkNotificationsToUser links notifications to a user.
     */
    @Test
    fun `linkNotificationsToUser should link notifications to user`() = runCoroutineTest {
        // Arrange
        val email = "${testPrefix}_link@test.com"
        val organizationId = createTestOrganization("org_$testPrefix", "")

        // Create notifications without user ID
        notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = email,
            organizationId = organizationId,
            notificationType = NotificationType.INVITE,
        ).registerNotificationForDeletion()

        notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = email,
            organizationId = organizationId,
            notificationType = NotificationType.SYSTEM,
        ).registerNotificationForDeletion()

        // Create user
        val userId = createTestUser("${testPrefix}_newuser@test.com")

        // Act
        val result = notificationDatastore.linkNotificationsToUser(email, userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow())

        // Verify notifications are now linked to user
        val userNotifications = notificationDatastore.getNotificationsForUser(userId, unreadOnly = false, limit = null)
        assertTrue(userNotifications.isSuccess)
        assertEquals(2, userNotifications.getOrThrow().size)
        assertTrue(userNotifications.getOrThrow().all { it.recipientUserId == userId })
    }

    /**
     * Tests that linkNotificationsToUser returns 0 when no notifications match.
     */
    @Test
    fun `linkNotificationsToUser should return 0 when no notifications match`() = runCoroutineTest {
        // Arrange
        val email = "${testPrefix}_nomatch@test.com"
        val userId = createTestUser("${testPrefix}_user@test.com")

        // Act
        val result = notificationDatastore.linkNotificationsToUser(email, userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow())
    }

    /**
     * Tests that linkNotificationsToUser only links notifications without a user ID.
     */
    @Test
    fun `linkNotificationsToUser should only link notifications without user ID`() = runCoroutineTest {
        // Arrange
        val email = "${testPrefix}_partial@test.com"
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val existingUserId = createTestUser("${testPrefix}_existing@test.com")
        val newUserId = createTestUser("${testPrefix}_new@test.com")

        // Create notification already linked to a user
        notificationDatastore.createNotification(
            recipientUserId = existingUserId,
            recipientEmail = email,
            organizationId = organizationId,
            notificationType = NotificationType.INVITE,
        ).registerNotificationForDeletion()

        // Create notification without user ID
        notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = email,
            organizationId = organizationId,
            notificationType = NotificationType.SYSTEM,
        ).registerNotificationForDeletion()

        // Act
        val result = notificationDatastore.linkNotificationsToUser(email, newUserId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow()) // Only one should be linked

        // Verify the existing one is still linked to the original user
        val existingUserNotifications = notificationDatastore.getNotificationsForUser(
            existingUserId,
            unreadOnly = false,
            limit = null
        )
        assertEquals(1, existingUserNotifications.getOrThrow().size)

        // Verify the new user has one notification
        val newUserNotifications = notificationDatastore.getNotificationsForUser(
            newUserId,
            unreadOnly = false,
            limit = null
        )
        assertEquals(1, newUserNotifications.getOrThrow().size)
    }
}
