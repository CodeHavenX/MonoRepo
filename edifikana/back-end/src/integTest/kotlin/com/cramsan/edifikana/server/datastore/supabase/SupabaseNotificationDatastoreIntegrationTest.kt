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
import kotlin.time.Duration.Companion.minutes
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
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val expiration = clock.now() + 5.minutes
        val inviteId = createTestInvite("${testPrefix}_invite@test.com", organizationId, expiration)
        val description = "You have been invited to join org_$testPrefix"

        // Act
        val result = notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = "${testPrefix}_recipient@test.com",
            notificationType = NotificationType.INVITE,
            description = description,
            inviteId = inviteId,
        ).registerNotificationForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val notification = result.getOrThrow()
        assertEquals(description, notification.description)
        assertEquals(inviteId, notification.inviteId)
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
        val userId = createTestUser("${testPrefix}_user@test.com")
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val expiration = clock.now() + 5.minutes
        val inviteId = createTestInvite("${testPrefix}_invite@test.com", organizationId, expiration)
        val description = "You have been invited to join org_$testPrefix"

        // Act
        val result = notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = null,
            notificationType = NotificationType.INVITE,
            description = description,
            inviteId = inviteId,
        ).registerNotificationForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val notification = result.getOrThrow()
        assertEquals(description, notification.description)
        assertEquals(userId, notification.recipientUserId)
        assertEquals(inviteId, notification.inviteId)
        assertEquals(NotificationType.INVITE, notification.notificationType)
        assertFalse(notification.isRead)
    }

    /**
     * Tests that getNotification returns the created notification.
     */
    @Test
    fun `getNotification should return created notification`() = runCoroutineTest {
        // Arrange
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val expiration = clock.now() + 5.minutes
        val inviteId = createTestInvite("${testPrefix}_invite@test.com", organizationId, expiration)
        val description = "You have been invited to join org_$testPrefix"
        val createResult = notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = "${testPrefix}_recipient@test.com",
            notificationType = NotificationType.INVITE,
            description = description,
            inviteId = inviteId,
        ).registerNotificationForDeletion()
        val created = createResult.getOrThrow()

        // Act
        val getResult = notificationDatastore.getNotification(created.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrThrow()
        assertNotNull(fetched)
        assertEquals(created.id, fetched.id)
        assertEquals(description, fetched.description)
        assertEquals(inviteId, fetched.inviteId)
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
        val expiration = clock.now() + 5.minutes
        val inviteId1 = createTestInvite("${testPrefix}_1@test.com", organizationId, expiration)
        val inviteId2 = createTestInvite("${testPrefix}_2@test.com", organizationId, expiration)

        // Create two notifications for the user
        notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = null,
            notificationType = NotificationType.INVITE,
            description = "Invite notification 1",
            inviteId = inviteId1,
        ).registerNotificationForDeletion()

        notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = null,
            notificationType = NotificationType.INVITE,
            description = "Invite notification 2",
            inviteId = inviteId2,
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
        val expiration = clock.now() + 5.minutes
        val inviteId1 = createTestInvite("${testPrefix}_1@test.com", organizationId, expiration)
        val inviteId2 = createTestInvite("${testPrefix}_2@test.com", organizationId, expiration)

        // Create first notification and mark as read
        val first = notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = null,
            notificationType = NotificationType.INVITE,
            description = "Invite notification 1",
            inviteId = inviteId1,
        ).registerNotificationForDeletion().getOrThrow()

        notificationDatastore.markAsRead(first.id)

        // Create second notification (unread)
        notificationDatastore.createNotification(
            recipientUserId = userId,
            recipientEmail = null,
            notificationType = NotificationType.INVITE,
            description = "Invite notification 2",
            inviteId = inviteId2,
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
        val expiration = clock.now() + 5.minutes

        // Create three notifications
        repeat(3) { i ->
            val inviteId = createTestInvite("${testPrefix}_$i@test.com", organizationId, expiration)
            notificationDatastore.createNotification(
                recipientUserId = userId,
                recipientEmail = null,
                notificationType = NotificationType.INVITE,
                description = "Invite notification $i",
                inviteId = inviteId,
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
     * Tests that markAsRead marks a notification as read.
     */
    @Test
    fun `markAsRead should mark notification as read`() = runCoroutineTest {
        // Arrange
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val expiration = clock.now() + 5.minutes
        val inviteId = createTestInvite("${testPrefix}_notify@test.com", organizationId, expiration)
        val created = notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = "${testPrefix}_recipient@test.com",
            notificationType = NotificationType.INVITE,
            description = "Test invite notification",
            inviteId = inviteId,
        ).registerNotificationForDeletion().getOrThrow()

        assertFalse(created.isRead)

        // Act
        val result = notificationDatastore.markAsRead(created.id)

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
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val expiration = clock.now() + 5.minutes
        val inviteId = createTestInvite("${testPrefix}_notify@test.com", organizationId, expiration)
        val created = notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = "${testPrefix}_recipient@test.com",
            notificationType = NotificationType.INVITE,
            description = "Test invite notification",
            inviteId = inviteId,
        ).getOrThrow() // Not registering for deletion since we're deleting it

        // Act
        val deleteResult = notificationDatastore.deleteNotification(created.id)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrThrow())

        // Verify it's gone
        val getResult = notificationDatastore.getNotification(created.id)
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
     * Tests that getNotificationByInvite returns notification for an invite.
     */
    @Test
    fun `getNotificationByInvite should return notification for invite`() = runCoroutineTest {
        // Arrange
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val expiration = clock.now() + 5.minutes
        val inviteId = createTestInvite("${testPrefix}_invite@test.com", organizationId, expiration)
        val description = "You have been invited to join org_$testPrefix"
        val createResult = notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = "${testPrefix}_recipient@test.com",
            notificationType = NotificationType.INVITE,
            description = description,
            inviteId = inviteId,
        ).registerNotificationForDeletion()
        val created = createResult.getOrThrow()

        // Act
        val getResult = notificationDatastore.getNotificationByInvite(inviteId)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrThrow()
        assertNotNull(fetched)
        assertEquals(created.id, fetched.id)
        assertEquals(inviteId, fetched.inviteId)
    }

    /**
     * Tests that getNotificationByInvite returns null for non-existent invite.
     */
    @Test
    fun `getNotificationByInvite should return null for non-existent invite`() = runCoroutineTest {
        // Arrange
        val fakeInviteId = com.cramsan.edifikana.lib.model.InviteId("00000000-0000-0000-0000-000000000000")

        // Act
        val result = notificationDatastore.getNotificationByInvite(fakeInviteId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow())
    }

    /**
     * Tests that getNotificationsByEmail returns notifications for an email address.
     */
    @Test
    fun `getNotificationsByEmail should return notifications for email`() = runCoroutineTest {
        // Arrange
        val recipientEmail = "${testPrefix}_recipient@test.com"
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val expiration = clock.now() + 5.minutes
        val inviteId1 = createTestInvite("${testPrefix}_1@test.com", organizationId, expiration)
        val inviteId2 = createTestInvite("${testPrefix}_2@test.com", organizationId, expiration)

        // Create two notifications for the same email (no user ID)
        notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = recipientEmail,
            notificationType = NotificationType.INVITE,
            description = "Invite notification 1",
            inviteId = inviteId1,
        ).registerNotificationForDeletion()

        notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = recipientEmail,
            notificationType = NotificationType.INVITE,
            description = "Invite notification 2",
            inviteId = inviteId2,
        ).registerNotificationForDeletion()

        // Act
        val result = notificationDatastore.getNotificationsByEmail(recipientEmail)

        // Assert
        assertTrue(result.isSuccess)
        val notifications = result.getOrThrow()
        assertEquals(2, notifications.size)
        assertTrue(notifications.all { it.recipientEmail == recipientEmail })
        assertTrue(notifications.all { it.recipientUserId == null })
    }

    /**
     * Tests that linkNotificationsToUser links notifications by email to a user.
     */
    @Test
    fun `linkNotificationsToUser should link notifications by email to user`() = runCoroutineTest {
        // Arrange
        val recipientEmail = "${testPrefix}_recipient@test.com"
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val expiration = clock.now() + 5.minutes
        val inviteId1 = createTestInvite("${testPrefix}_1@test.com", organizationId, expiration)
        val inviteId2 = createTestInvite("${testPrefix}_2@test.com", organizationId, expiration)

        // Create notifications for an email (simulating notifications before user signup)
        val notification1 = notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = recipientEmail,
            notificationType = NotificationType.INVITE,
            description = "Invite notification 1",
            inviteId = inviteId1,
        ).registerNotificationForDeletion().getOrThrow()

        val notification2 = notificationDatastore.createNotification(
            recipientUserId = null,
            recipientEmail = recipientEmail,
            notificationType = NotificationType.INVITE,
            description = "Invite notification 2",
            inviteId = inviteId2,
        ).registerNotificationForDeletion().getOrThrow()

        // Verify notifications have no user ID initially
        assertNull(notification1.recipientUserId)
        assertNull(notification2.recipientUserId)

        // Create a user (simulating user signup)
        val userId = createTestUser(recipientEmail)

        // Act - Link notifications to the new user
        val linkResult = notificationDatastore.linkNotificationsToUser(recipientEmail, userId)

        // Assert
        assertTrue(linkResult.isSuccess)
        assertEquals(2, linkResult.getOrThrow())

        // Verify notifications are now linked to the user
        val userNotifications = notificationDatastore.getNotificationsForUser(userId, unreadOnly = false, limit = null)
        assertTrue(userNotifications.isSuccess)
        val notifications = userNotifications.getOrThrow()
        assertEquals(2, notifications.size)
        assertTrue(notifications.all { it.recipientUserId == userId })
    }

    /**
     * Tests that linkNotificationsToUser returns 0 when no notifications exist for email.
     */
    @Test
    fun `linkNotificationsToUser should return 0 when no notifications exist`() = runCoroutineTest {
        // Arrange
        val email = "${testPrefix}_nonexistent@test.com"
        val userId = createTestUser("${testPrefix}_user@test.com")

        // Act
        val result = notificationDatastore.linkNotificationsToUser(email, userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow())
    }

    /**
     * Tests that linkNotificationsToUser does not link notifications already assigned to a user.
     */
    @Test
    fun `linkNotificationsToUser should not link notifications already assigned to a user`() = runCoroutineTest {
        // Arrange
        val recipientEmail = "${testPrefix}_recipient@test.com"
        val existingUserId = createTestUser("${testPrefix}_existing@test.com")
        val newUserId = createTestUser(recipientEmail)
        val organizationId = createTestOrganization("org_$testPrefix", "")
        val expiration = clock.now() + 5.minutes
        val inviteId = createTestInvite("${testPrefix}_invite@test.com", organizationId, expiration)

        // Create a notification already assigned to an existing user
        notificationDatastore.createNotification(
            recipientUserId = existingUserId,
            recipientEmail = recipientEmail,
            notificationType = NotificationType.INVITE,
            description = "Already assigned notification",
            inviteId = inviteId,
        ).registerNotificationForDeletion()

        // Act - Try to link notifications for the same email to a different user
        val linkResult = notificationDatastore.linkNotificationsToUser(recipientEmail, newUserId)

        // Assert - Should return 0 since notification already has a user ID
        assertTrue(linkResult.isSuccess)
        assertEquals(0, linkResult.getOrThrow())

        // Verify original user still owns the notification
        val existingUserNotifications = notificationDatastore.getNotificationsForUser(
            existingUserId,
            unreadOnly = false,
            limit = null
        )
        assertEquals(1, existingUserNotifications.getOrThrow().size)

        // Verify new user has no notifications
        val newUserNotifications = notificationDatastore.getNotificationsForUser(
            newUserId,
            unreadOnly = false,
            limit = null
        )
        assertEquals(0, newUserNotifications.getOrThrow().size)
    }
}
