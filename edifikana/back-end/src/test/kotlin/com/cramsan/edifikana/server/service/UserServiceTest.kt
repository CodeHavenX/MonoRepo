package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.datastore.UserDatastore
import com.cramsan.edifikana.server.service.models.Organization
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.asClock
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
        organizationDatastore = mockk()
        testTimeSource = TestTimeSource()
        clock = testTimeSource.asClock(2024, 1, 1, 0, 0)
        userService = UserService(userDatastore, organizationDatastore, clock)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that createUser creates a transient user.
     */
    @Test
    fun `createUser should create user`() = runTest {
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
        coEvery {
            organizationDatastore.createOrganization()
        } returns Result.success(mockk())

        // Act
        val result = userService.createUser(email, phone, password, firstName, lastName)

        // Assert
        assertTrue(result.isSuccess)
        coVerify {
            userDatastore.createUser(
                email,
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        }
        verify { organizationDatastore wasNot Called }
    }

    /**
     * Tests that createUser creates a user and organization.
     */
    @Test
    fun `createUser should create user and organization`() = runTest {
        // Arrange
        val email = "test@example.com"
        val phone = "1234567890"
        val password = "Asd!@#123"
        val firstName = "John"
        val lastName = "Doe"
        val user = mockk<User>()
        val organization = mockk<Organization>()
        val userId = UserId("id")
        val orgId = OrganizationId("orgId")
        val role = UserRole.OWNER
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
        every { organization.id } returns orgId
        coEvery {
            organizationDatastore.createOrganization()
        } returns Result.success(organization)
        coEvery { organizationDatastore.addUserToOrganization(userId, orgId, role) } returns Result.success(Unit)

        // Act
        val result = userService.createUser(email, phone, password, firstName, lastName)

        // Assert
        assertTrue(result.isSuccess)
        coVerify {
            userDatastore.createUser(
                email,
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        }
        coVerify { organizationDatastore.createOrganization() }
        coVerify { organizationDatastore.addUserToOrganization(userId, orgId, role) }
    }

    /**
     * Tests that getUser retrieves a user from the database.
     */
    @Test
    fun `getUser should return user from database`() = runTest {
        // Arrange
        val userId = UserId("id")
        val user = mockk<User>()
        coEvery { userDatastore.getUser(any()) } returns Result.success(user)

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
        val expirationTime = clock.now() + 14.days

        coEvery { userDatastore.recordInvite(email, orgId, expirationTime) } returns Result.success(mockk())

        // Act
        val result = userService.inviteUser(email, orgId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { userDatastore.recordInvite(email, orgId, any()) }
    }

    /**
     * Tests that inviteUser returns failure when recordInvite fails.
     */
    @Test
    fun `inviteUser should return failure when recordInvite fails`() = runTest {
        // Arrange
        val email = "invite@example.com"
        val orgId = OrganizationId("orgId")
        val expirationTime = clock.now() + 14.days

        val error = Exception("Failed to record invite")
        coEvery { userDatastore.recordInvite(email, orgId, expirationTime) } returns Result.failure(error)

        // Act
        val result = userService.inviteUser(email, orgId)

        // Assert
        assertTrue(result.isFailure)
        coVerify { userDatastore.recordInvite(email, orgId, any()) }
    }
}
