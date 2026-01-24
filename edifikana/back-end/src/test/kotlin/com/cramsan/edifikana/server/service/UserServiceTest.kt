package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.NotificationDatastore
import com.cramsan.edifikana.server.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.datastore.UserDatastore
import com.cramsan.edifikana.server.service.models.Invite
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.asClock
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource

/**
 * Test class for [UserService].
 */
@OptIn(ExperimentalTime::class)
class UserServiceTest {
    private lateinit var userDatastore: UserDatastore
    private lateinit var userService: UserService
    private lateinit var notificationDatastore: NotificationDatastore
    private lateinit var organizationDatastore: OrganizationDatastore
    private lateinit var testTimeSource: TestTimeSource
    private lateinit var clock: Clock

    /**
     * Sets up the test environment by initializing mocks for [UserDatastore] and [userService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        userDatastore = mockk()
        notificationDatastore = mockk()
        organizationDatastore = mockk()
        testTimeSource = TestTimeSource()
        clock = testTimeSource.asClock(2024, 1, 1, 0, 0)
        userService = UserService(userDatastore, notificationDatastore, organizationDatastore, clock)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that createUser creates a transient user (no password).
     */
    @Test
    fun `createUser should create transient user when password is blank`() = runTest {
        // Arrange
        val email = "test@example.com"
        val phone = "1234567890"
        val password = ""
        val firstName = "John"
        val lastName = "Doe"
        val user = mockk<User>()
        coEvery {
            userDatastore.createUser(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.success(user)
        every { user.id } returns UserId("id")

        // Act
        val result = userService.createUser(email, phone, password, firstName, lastName)

        // Assert
        assertTrue(result.isSuccess)
        coVerify {
            userDatastore.createUser(
                email,
                phone,
                password,
                firstName,
                lastName,
                true, // isTransient = true
            )
        }
        coVerify(exactly = 0) { notificationDatastore.linkNotificationsToUser(any(), any()) }
    }

    /**
     * Tests that createUser creates a non-transient user and links notifications.
     */
    @Test
    fun `createUser should create user and link notifications when password is provided`() = runTest {
        // Arrange
        val email = "test@example.com"
        val phone = "1234567890"
        val password = "Asd!@#123"
        val firstName = "John"
        val lastName = "Doe"
        val user = mockk<User>()
        val userId = UserId("id")
        coEvery {
            userDatastore.createUser(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.success(user)
        every { user.id } returns userId
        coEvery { notificationDatastore.linkNotificationsToUser(email, userId) } returns Result.success(0)

        // Act
        val result = userService.createUser(email, phone, password, firstName, lastName)

        // Assert
        assertTrue(result.isSuccess)
        coVerify {
            userDatastore.createUser(
                email,
                phone,
                password,
                firstName,
                lastName,
                false, // isTransient = false
            )
        }
        coVerify { notificationDatastore.linkNotificationsToUser(email, userId) }
    }

    /**
     * Tests that getUser retrieves a user from the database.
     */
    @Test
    fun `getUser should return user from database`() = runTest {
        // Arrange
        val userId = UserId("id")
        val user = mockk<User>()
        coEvery { userDatastore.getUser(any<UserId>()) } returns Result.success(user)

        // Act
        val result = userService.getUser(userId)

        // Assert
        assertEquals(Result.success(user), result)
        coVerify { userDatastore.getUser(userId) }
    }

    /**
     * Tests that getUsers retrieves all users from a given organization.
     */
    @Test
    fun `getUsers should return all users`() = runTest {
        // Arrange
        val users = listOf(mockk<User>(), mockk())
        val orgId = OrganizationId("orgId")
        coEvery { userDatastore.getUsers(orgId) } returns Result.success(users)

        // Act
        val result = userService.getUsers(orgId)

        // Assert
        assertEquals(Result.success(users), result)
        coVerify { userDatastore.getUsers(orgId) }
    }

    /**
     * Tests that updateUser updates a user and returns the updated user.
     */
    @Test
    fun `updateUser should update user and return result`() = runTest {
        // Arrange
        val userId = UserId("id")
        val email = "new@email.com"
        val user = mockk<User>()
        coEvery {
            userDatastore.updateUser(
                any(),
                any(),
            )
        } returns Result.success(user)

        // Act
        val result = userService.updateUser(userId, email)

        // Assert
        assertEquals(Result.success(user), result)
        coVerify {
            userDatastore.updateUser(
                userId,
                email,
            )
        }
    }

    /**
     * Tests that deleteUser deletes a user and returns the result.
     */
    @Test
    fun `deleteUser should delete user and return result`() = runTest {
        // Arrange
        val userId = UserId("id")
        coEvery { userDatastore.deleteUser(any()) } returns Result.success(true)

        // Act
        val result = userService.deleteUser(userId)

        // Assert
        assertTrue(result.getOrThrow())
        coVerify { userDatastore.deleteUser(userId) }
    }

    /**
     * Tests that updatePassword updates a user's password and returns the result.
     */
    @OptIn(SecureStringAccess::class)
    @Test
    fun `updatePassword should update password and return result`() = runTest {
        // Arrange
        val userId = UserId("id")
        val password = SecureString("newpass")
        val hashedPassword = SecureString("12345678")
        coEvery { userDatastore.updatePassword(any(), any(), any()) } returns Result.success(Unit)

        // Act
        val result = userService.updatePassword(
            userId,
            hashedPassword,
            password,
        )

        // Assert
        assertTrue(result.isSuccess)
        coVerify {
            userDatastore.updatePassword(
                userId,
                hashedPassword,
                password,
            )
        }
    }

    /**
     * Tests that inviteUser records an invite and returns success.
     */
    @Test
    fun `inviteUser should record invite and return success`() = runTest {
        // Arrange
        val email = "invite@example.com"
        val orgId = OrganizationId("orgId")
        val userId = UserId("id")
        val user = mockk<User>()
        val role = UserRole.USER
        val inviteId = InviteId("inviteId")
        val invite = mockk<Invite>()
        val organization = Organization(id = orgId, name = "Test Org", description = "Test Description")

        val notificationId = com.cramsan.edifikana.lib.model.NotificationId("notifId")
        val createdNotification = mockk<com.cramsan.edifikana.server.service.models.Notification>()
        every { createdNotification.id } returns notificationId

        every { user.id } returns userId
        every { invite.id } returns inviteId
        coEvery { userDatastore.getUser(email) } returns Result.success(user)
        coEvery { organizationDatastore.getOrganization(orgId) } returns Result.success(organization)
        coEvery { userDatastore.recordInvite(email, orgId, any(), role) } returns Result.success(invite)
        coEvery {
            notificationDatastore.createNotification(
                recipientUserId = any(),
                notificationType = any(),
                description = any(),
                inviteId = any(),
            )
        } returns Result.success(createdNotification)

        // Act
        val result = userService.inviteUser(email, orgId, role)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { userDatastore.recordInvite(email, orgId, any(), role) }
        coVerify {
            notificationDatastore.createNotification(
                recipientUserId = any(),
                notificationType = any(),
                description = any(),
                inviteId = any(),
            )
        }
    }

    /**
     * Tests that inviteUser returns failure when recordInvite fails.
     */
    @Test
    fun `inviteUser should return failure when recordInvite fails`() = runTest {
        // Arrange
        val email = "invite@example.com"
        val orgId = OrganizationId("orgId")
        val userId = UserId("id")
        val user = mockk<User>()
        val role = UserRole.USER
        val organization = Organization(id = orgId, name = "Test Org", description = "Test Description")

        val error = Exception("Failed to record invite")
        coEvery { userDatastore.recordInvite(email, orgId, any(), role) } returns Result.failure(error)
        coEvery { user.id } returns userId
        coEvery { userDatastore.getUser(email) } returns Result.success(user)
        coEvery { organizationDatastore.getOrganization(orgId) } returns Result.success(organization)

        // Act
        val result = userService.inviteUser(email, orgId, role)

        // Assert
        assertTrue(result.isFailure)
        coVerify { userDatastore.recordInvite(email, orgId, any(), role) }
    }

    /**
     * Tests that checkUserIsRegistered returns true when a user is present for the email.
     */
    @Test
    fun `checkUserIsRegistered returns true when user exists`() = runTest {
        // Arrange
        val email = "exists@example.com"
        val user = mockk<User>()
        coEvery { userDatastore.getUser(email) } returns Result.success<User?>(user)

        // Act
        val result = userService.checkUserIsRegistered(email)

        // Assert
        assertEquals(Result.success(true), result)
        coVerify { userDatastore.getUser(email) }
    }

    /**
     * Tests that checkUserIsRegistered returns false when no user is present for the email.
     */
    @Test
    fun `checkUserIsRegistered returns false when user does not exist`() = runTest {
        // Arrange
        val email = "notfound@example.com"
        coEvery { userDatastore.getUser(email) } returns Result.success<User?>(null)

        // Act
        val result = userService.checkUserIsRegistered(email)

        // Assert
        assertEquals(Result.success(false), result)
        coVerify { userDatastore.getUser(email) }
    }

    /**
     * Tests that acceptInvite successfully adds user to organization.
     */
    @Test
    fun `acceptInvite should add user to organization when invite is valid`() = runTest {
        // Arrange
        val userId = UserId("user123")
        val inviteId = InviteId("invite123")
        val orgId = OrganizationId("org123")
        val email = "test@example.com"
        val futureTime = clock.now() + 7.days
        val notificationId = com.cramsan.edifikana.lib.model.NotificationId("notif123")

        val invite = Invite(
            id = inviteId,
            email = email,
            organizationId = orgId,
            role = UserRole.USER,
            expiration = futureTime,
        )
        val user = mockk<User>()
        every { user.email } returns email

        val notification = mockk<com.cramsan.edifikana.server.service.models.Notification>()
        every { notification.id } returns notificationId

        coEvery { userDatastore.getInvite(inviteId) } returns Result.success(invite)
        coEvery { userDatastore.getUser(userId) } returns Result.success(user)
        coEvery { organizationDatastore.addUserToOrganization(userId, orgId, UserRole.USER) } returns Result.success(Unit)
        coEvery { notificationDatastore.getNotificationByInvite(inviteId) } returns Result.success(notification)
        coEvery { notificationDatastore.deleteNotification(notificationId) } returns Result.success(true)
        coEvery { userDatastore.removeInvite(inviteId) } returns Result.success(Unit)

        // Act
        val result = userService.acceptInvite(userId, inviteId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { organizationDatastore.addUserToOrganization(userId, orgId, UserRole.USER) }
        coVerify { notificationDatastore.deleteNotification(notificationId) }
        coVerify { userDatastore.removeInvite(inviteId) }
    }

    /**
     * Tests that acceptInvite fails when invite is not found.
     */
    @Test
    fun `acceptInvite should fail when invite is not found`() = runTest {
        // Arrange
        val userId = UserId("user123")
        val inviteId = InviteId("invite123")

        coEvery { userDatastore.getInvite(inviteId) } returns Result.success(null)

        // Act
        val result = userService.acceptInvite(userId, inviteId)

        // Assert
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { organizationDatastore.addUserToOrganization(any(), any(), any()) }
    }

    /**
     * Tests that acceptInvite fails when invite is expired.
     */
    @Test
    fun `acceptInvite should fail when invite is expired`() = runTest {
        // Arrange
        val userId = UserId("user123")
        val inviteId = InviteId("invite123")
        val orgId = OrganizationId("org123")
        val email = "test@example.com"
        val pastTime = clock.now() - 1.days

        val invite = Invite(
            id = inviteId,
            email = email,
            organizationId = orgId,
            role = UserRole.USER,
            expiration = pastTime, // Expired
        )

        coEvery { userDatastore.getInvite(inviteId) } returns Result.success(invite)

        // Act
        val result = userService.acceptInvite(userId, inviteId)

        // Assert
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { organizationDatastore.addUserToOrganization(any(), any(), any()) }
    }

    /**
     * Tests that acceptInvite fails when user email does not match invite email.
     */
    @Test
    fun `acceptInvite should fail when user email does not match invite`() = runTest {
        // Arrange
        val userId = UserId("user123")
        val inviteId = InviteId("invite123")
        val orgId = OrganizationId("org123")
        val inviteEmail = "invite@example.com"
        val userEmail = "different@example.com"
        val futureTime = clock.now() + 7.days

        val invite = Invite(
            id = inviteId,
            email = inviteEmail,
            organizationId = orgId,
            role = UserRole.USER,
            expiration = futureTime,
        )
        val user = mockk<User>()
        every { user.email } returns userEmail

        coEvery { userDatastore.getInvite(inviteId) } returns Result.success(invite)
        coEvery { userDatastore.getUser(userId) } returns Result.success(user)

        // Act
        val result = userService.acceptInvite(userId, inviteId)

        // Assert
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { organizationDatastore.addUserToOrganization(any(), any(), any()) }
    }

    /**
     * Tests that declineInvite removes the invite when valid.
     */
    @Test
    fun `declineInvite should remove invite when user email matches`() = runTest {
        // Arrange
        val userId = UserId("user123")
        val inviteId = InviteId("invite123")
        val orgId = OrganizationId("org123")
        val email = "test@example.com"
        val futureTime = clock.now() + 7.days
        val notificationId = com.cramsan.edifikana.lib.model.NotificationId("notif123")

        val invite = Invite(
            id = inviteId,
            email = email,
            organizationId = orgId,
            role = UserRole.USER,
            expiration = futureTime,
        )
        val user = mockk<User>()
        every { user.email } returns email

        val notification = mockk<com.cramsan.edifikana.server.service.models.Notification>()
        every { notification.id } returns notificationId

        coEvery { userDatastore.getInvite(inviteId) } returns Result.success(invite)
        coEvery { userDatastore.getUser(userId) } returns Result.success(user)
        coEvery { notificationDatastore.getNotificationByInvite(inviteId) } returns Result.success(notification)
        coEvery { notificationDatastore.deleteNotification(notificationId) } returns Result.success(true)
        coEvery { userDatastore.removeInvite(inviteId) } returns Result.success(Unit)

        // Act
        val result = userService.declineInvite(userId, inviteId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { notificationDatastore.deleteNotification(notificationId) }
        coVerify { userDatastore.removeInvite(inviteId) }
    }

    /**
     * Tests that declineInvite fails when invite is not found.
     */
    @Test
    fun `declineInvite should fail when invite is not found`() = runTest {
        // Arrange
        val userId = UserId("user123")
        val inviteId = InviteId("invite123")

        coEvery { userDatastore.getInvite(inviteId) } returns Result.success(null)

        // Act
        val result = userService.declineInvite(userId, inviteId)

        // Assert
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { userDatastore.removeInvite(any()) }
    }

    /**
     * Tests that declineInvite fails when user email does not match invite email.
     */
    @Test
    fun `declineInvite should fail when user email does not match invite`() = runTest {
        // Arrange
        val userId = UserId("user123")
        val inviteId = InviteId("invite123")
        val orgId = OrganizationId("org123")
        val inviteEmail = "invite@example.com"
        val userEmail = "different@example.com"
        val futureTime = clock.now() + 7.days

        val invite = Invite(
            id = inviteId,
            email = inviteEmail,
            organizationId = orgId,
            role = UserRole.USER,
            expiration = futureTime,
        )
        val user = mockk<User>()
        every { user.email } returns userEmail

        coEvery { userDatastore.getInvite(inviteId) } returns Result.success(invite)
        coEvery { userDatastore.getUser(userId) } returns Result.success(user)

        // Act
        val result = userService.declineInvite(userId, inviteId)

        // Assert
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { userDatastore.removeInvite(any()) }
    }

    /**
     * Tests that getInviteOrganization returns the organization ID for the invite.
     */
    @Test
    fun `getInviteOrganization should return organization ID`() = runTest {
        // Arrange
        val orgId = OrganizationId("org123")
        val inviteId = InviteId("invite123")
        val email = "test@example.com"
        val futureTime = clock.now() + 7.days

        val invite = Invite(
            id = inviteId,
            email = email,
            organizationId = orgId,
            role = UserRole.USER,
            expiration = futureTime,
        )

        coEvery { userDatastore.getInvite(inviteId) } returns Result.success(invite)

        // Act
        val result = userService.getInviteOrganization(inviteId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(orgId, result.getOrNull())
    }

    /**
     * Tests that getInviteOrganization fails when invite is not found.
     */
    @Test
    fun `getInviteOrganization should fail when invite is not found`() = runTest {
        // Arrange
        val inviteId = InviteId("invite123")

        coEvery { userDatastore.getInvite(inviteId) } returns Result.success(null)

        // Act
        val result = userService.getInviteOrganization(inviteId)

        // Assert
        assertTrue(result.isFailure)
    }

    /**
     * Tests that cancelInvite removes the invite.
     */
    @Test
    fun `cancelInvite should remove invite`() = runTest {
        // Arrange
        val orgId = OrganizationId("org123")
        val inviteId = InviteId("invite123")
        val email = "test@example.com"
        val futureTime = clock.now() + 7.days

        val invite = Invite(
            id = inviteId,
            email = email,
            organizationId = orgId,
            role = UserRole.USER,
            expiration = futureTime,
        )

        coEvery { userDatastore.getInvite(inviteId) } returns Result.success(invite)
        coEvery { userDatastore.removeInvite(inviteId) } returns Result.success(Unit)

        // Act
        val result = userService.cancelInvite(inviteId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { userDatastore.removeInvite(inviteId) }
    }

    /**
     * Tests that cancelInvite fails when invite is not found.
     */
    @Test
    fun `cancelInvite should fail when invite is not found`() = runTest {
        // Arrange
        val inviteId = InviteId("invite123")

        coEvery { userDatastore.getInvite(inviteId) } returns Result.success(null)

        // Act
        val result = userService.cancelInvite(inviteId)

        // Assert
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { userDatastore.removeInvite(any()) }
    }
}
