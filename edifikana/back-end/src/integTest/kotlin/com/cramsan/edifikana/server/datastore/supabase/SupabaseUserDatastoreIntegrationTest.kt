package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.core.Hashing
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import com.cramsan.framework.utils.uuid.UUID
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.OTP
import org.junit.jupiter.api.assertInstanceOf
import org.koin.test.inject
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.TestTimeSource

@OptIn(ExperimentalTime::class)
class SupabaseUserDatastoreIntegrationTest : SupabaseIntegrationTest() {
    private val auth: Auth by inject()
    private val testTimeSource: TestTimeSource by inject()
    private val clock: Clock by inject()

    private lateinit var test_prefix: String

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
    }

    @Test
    fun `createUser with provided password should return user on success`() = runCoroutineTest {
        // Arrange

        // Act
        val result = userDatastore.createUser(
            email = "${test_prefix}_user@test.com",
            phoneNumber = "123-456-7890",
            password = "Password1!",
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
            isTransient = false,
        ).registerUserForDeletion()

        // Assert
        assertEquals(
            User(
                id = result.getOrThrow().id,
                email = "${test_prefix}_user@test.com",
                phoneNumber = "123-456-7890",
                firstName = "${test_prefix}_First",
                lastName = "${test_prefix}_Last",
                authMetadata = User.AuthMetadata(isPasswordSet = true),
                role = UserRole.USER,
            ),
            result.getOrNull(),
        )
    }

    @Test
    fun `createUser with null password should succeed with a temp user pending association`() = runCoroutineTest {
        // Arrange

        // Act
        val result = userDatastore.createUser(
            email = "${test_prefix}_user@test.com",
            phoneNumber = "123-456-7890",
            password = null, // No password provided
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
            isTransient = true,
        ).registerUserForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrThrow()
        assertEquals(
            User(
                id = user.id,
                email = "${test_prefix}_user@test.com",
                phoneNumber = "123-456-7890",
                firstName = "${test_prefix}_First",
                lastName = "${test_prefix}_Last",
                authMetadata = User.AuthMetadata(isPasswordSet = false),
                role = UserRole.USER
            ),
            user,
        )
    }

    @Test
    fun `createUser should fail with existing email`() = runCoroutineTest {
        // Arrange

        // Act
        val first = userDatastore.createUser(
            email = "${test_prefix}_dupe@test.com",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
            isTransient = false,
        ).registerUserForDeletion()
        assertTrue(first.isSuccess)
        val second = userDatastore.createUser(
            email = "${test_prefix}_dupe@test.com",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
            isTransient = false,
        )

        // Assert
        assertTrue(second.isFailure)
        assertInstanceOf<ClientRequestExceptions.ConflictException>(second.exceptionOrNull())
    }

    @Test
    fun `createUser should fail with invalid email`() = runCoroutineTest {
        // Arrange

        // Act
        val result = userDatastore.createUser(
            email = "not-an-email",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "Invalid",
            lastName = "User",
            isTransient = false,
        )

        // Assert
        assertTrue(result.isFailure)
        assertInstanceOf<AuthRestException>(result.exceptionOrNull())
    }

    @Test
    fun `getUser should return created user`() = runCoroutineTest {
        // Arrange

        // Act
        val createResult = userDatastore.createUser(
            email = "${test_prefix}_getuser@test.com",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "Get",
            lastName = "User",
            isTransient = false,
        ).registerUserForDeletion()
        assertTrue(createResult.isSuccess)
        val user = createResult.getOrNull()!!
        val getResult = userDatastore.getUser(user.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertTrue(fetched != null && fetched.email == "${test_prefix}_getuser@test.com")
    }

    /**
     * Tests that getUser(email) returns the created user when the email exists.
     */
    @Test
    fun `getUser by email should return created user`() = runCoroutineTest {
        // Arrange
        val email = "${test_prefix}_byemail@test.com"

        // Act
        val createResult = userDatastore.createUser(
            email = email,
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "By",
            lastName = "Email",
            isTransient = false,
        ).registerUserForDeletion()

        // Assert create succeeded
        assertTrue(createResult.isSuccess)
        val created = createResult.getOrThrow()

        // Act: fetch by email
        val fetchResult = userDatastore.getUser(email)

        // Assert
        assertTrue(fetchResult.isSuccess)
        val fetched = fetchResult.getOrNull()
        assertTrue(fetched != null)
        assertEquals(created.id, fetched!!.id)
        assertEquals(email, fetched.email)
    }

    /**
     * Tests that getUser(email) returns null when the email is not present in the database.
     */
    @Test
    fun `getUser by email should return null when not found`() = runCoroutineTest {
        // Arrange
        val email = "${test_prefix}_noexist@test.com"

        // Act
        val fetchResult = userDatastore.getUser(email)

        // Assert
        assertTrue(fetchResult.isSuccess)
        assertTrue(fetchResult.getOrNull() == null)
    }

    @Test
    fun `deleteUser should remove user`() = runCoroutineTest {
        // Arrange

        // Act
        val createResult = userDatastore.createUser(
            email = "${test_prefix}_delete@test.com",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "Delete",
            lastName = "User",
            isTransient = false,
        )
        assertTrue(createResult.isSuccess)
        val user = createResult.getOrNull()!!
        val deleteResult = userDatastore.deleteUser(user.id)

        // Assert
        assertTrue(deleteResult.isSuccess && deleteResult.getOrNull() == true)
        val getResult = userDatastore.getUser(user.id)
        assertTrue(getResult.isSuccess && getResult.getOrNull() == null)


        // Clean up
        userDatastore.purgeUser(user.id)
    }

    @Test
    fun `deleteUser should fail for non-existent user`() = runCoroutineTest {
        // Arrange
        val fakeId = UserId(test_prefix)

        // Act
        val deleteResult = userDatastore.deleteUser(fakeId)

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
        assertInstanceOf<ClientRequestExceptions.NotFoundException>(deleteResult.exceptionOrNull())
    }

    @Test
    fun `associateUser should fail for non-existent Supabase user`() = runCoroutineTest {
        // Arrange: Use an email that does not exist

        // Act
        val associateResult = userDatastore.associateUser(
            userId = UserId(test_prefix),
            email = "${test_prefix}_notfound@test.com",
        )

        // Assert
        assertTrue(associateResult.isFailure)
        assertInstanceOf<ClientRequestExceptions.NotFoundException>(associateResult.exceptionOrNull())
    }

    // We cannot test this in the integration test because it requires a Supabase user to be created first.
    // Right now we can create the user but we are not able to retrieve the user ID from Supabase Auth.
    @Ignore
    @Test
    fun `associateUser should associate a pending user with a Supabase user`() = runCoroutineTest {
        // Arrange: Create a user with a password (Supabase user)
        val email = "${test_prefix}@test.com"
        val createResult = userDatastore.createUser(
            email = email,
            phoneNumber = "123-456-7890",
            password = null,
            firstName = "Associate",
            lastName = "User",
            isTransient = true,
        ).registerUserForDeletion()
        val createdUser = createResult.getOrNull()!!
        auth.signInWith(OTP) {
            this.email = email
            createUser = true // This will create a user in Supabase Auth.
        }
        val supabaseUserId = auth.currentUserOrNull()!!.id
        registerSupabaseUserForDeletion(supabaseUserId)

        // Act: Try to associate the created Supabase user with the pending user's email
        val associateResult = userDatastore.associateUser(
            userId = UserId(supabaseUserId),
            email = email,
        )

        // Assert
        assertTrue(associateResult.isSuccess)
        val associatedUser = associateResult.getOrThrow()
        assertEquals(createdUser.id, associatedUser.id)
        assertEquals(createdUser.email, associatedUser.email)
        assertEquals(createdUser.phoneNumber, associatedUser.phoneNumber)
        assertEquals(createdUser.firstName, associatedUser.firstName)
        assertEquals(createdUser.lastName, associatedUser.lastName)
    }

    @OptIn(SecureStringAccess::class)
    @Test
    fun `update password should update the user's password when one is already set`() = runCoroutineTest {
        // Arrange: Create a user with a password
        val email = "${test_prefix}@test.com"
        val oldPassword = "oldPassword1!"
        val createResult = userDatastore.createUser(
            email = email,
            phoneNumber = "123-456-7890",
            password = oldPassword,
            firstName = "Associate",
            lastName = "User",
            isTransient = false,
        ).registerUserForDeletion()
        val user = createResult.getOrThrow()

        val currentPasswordHashed = Hashing.insecureHash(oldPassword.encodeToByteArray()).toString()

        // Act: Update the user's password
        val updateResult = userDatastore.updatePassword(
            id = UserId(user.id.userId),
            currentHashedPassword = SecureString(currentPasswordHashed),
            newPassword = SecureString("NewPassword1!"),
        )

        // Assert: Check if the password was updated successfully
        assertTrue(updateResult.isSuccess, "Password update should succeed")
    }

    @Test
    fun `recordInvite with valid data should succeed`() = runCoroutineTest {
        // Arrange
        val organizationId = createTestOrganization("org_$test_prefix", "")
        val expiration = clock.now() + 1.minutes // 1 minute in the future
        val email = "${test_prefix}_invite@test.com"

        // Act
        val result = userDatastore.recordInvite(
            email = email,
            organizationId = organizationId,
            expiration = expiration,
            role = UserRole.USER,
        ).registerInviteForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val invite = result.getOrThrow()
        assertEquals(email, invite.email)
        assertEquals(organizationId, invite.organizationId)
        assertEquals(UserRole.USER, invite.role)
    }

    @Test
    fun `recordInvite with manager role should succeed`() = runCoroutineTest {
        // Arrange
        val organizationId = createTestOrganization("org_$test_prefix", "")
        val expiration = clock.now() + 1.minutes
        val email = "${test_prefix}_manager_invite@test.com"

        // Act
        val result = userDatastore.recordInvite(
            email = email,
            organizationId = organizationId,
            expiration = expiration,
            role = UserRole.MANAGER,
        ).registerInviteForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val invite = result.getOrThrow()
        assertEquals(UserRole.MANAGER, invite.role)
    }

    @Test
    fun `getInvites should return recorded invite`() = runCoroutineTest {
        // Arrange
        val organizationId = createTestOrganization("org_$test_prefix", "")
        val email = "${test_prefix}_invite2@test.com"
        val expiration = clock.now() + 2.minutes // 2 minute in the future
        userDatastore.recordInvite(
            email = email,
            organizationId = organizationId,
            expiration = expiration,
            role = UserRole.USER,
        ).registerInviteForDeletion()

        // Act
        testTimeSource +=  1.minutes
        val invitesResult = userDatastore.getInvites(organizationId)

        // Assert
        assertTrue(invitesResult.isSuccess)
        val invites = invitesResult.getOrThrow()
        assertTrue(invites.any { it.email == email })
    }

    @Test
    fun `getInvites for organization with expired invites should return empty list`() = runCoroutineTest {
        // Arrange
        val organizationId = createTestOrganization("org_$test_prefix", "")
        val email = "${test_prefix}_invite2@test.com"
        val expiration = clock.now() + 2.minutes // 2 minute in the future
        userDatastore.recordInvite(
            email = email,
            organizationId = organizationId,
            expiration = expiration,
            role = UserRole.USER,
        ).registerInviteForDeletion()

        // Act
        testTimeSource +=  5.minutes
        val invitesResult = userDatastore.getInvites(organizationId)

        // Assert
        assertTrue(invitesResult.isSuccess)
        val invites = invitesResult.getOrThrow()
        assertTrue(invites.isEmpty())
    }

    @Test
    fun `getInvite should return invite by ID`() = runCoroutineTest {
        // Arrange
        val organizationId = createTestOrganization("org_$test_prefix", "")
        val email = "${test_prefix}_getinvite@test.com"
        val expiration = clock.now() + 5.minutes
        val recordResult = userDatastore.recordInvite(
            email = email,
            organizationId = organizationId,
            expiration = expiration,
            role = UserRole.MANAGER,
        ).registerInviteForDeletion()
        val createdInvite = recordResult.getOrThrow()

        // Act
        val getResult = userDatastore.getInvite(createdInvite.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val invite = getResult.getOrThrow()
        assertTrue(invite != null)
        assertEquals(createdInvite.id, invite.id)
        assertEquals(email, invite.email)
        assertEquals(organizationId, invite.organizationId)
        assertEquals(UserRole.MANAGER, invite.role)
    }

    @Test
    fun `getInvite should return null for non-existent ID`() = runCoroutineTest {
        // Arrange - Use valid UUID format that doesn't exist in database
        val nonExistentId = InviteId("00000000-0000-0000-0000-000000000000")

        // Act
        val getResult = userDatastore.getInvite(nonExistentId)

        // Assert
        assertTrue(getResult.isSuccess)
        assertTrue(getResult.getOrNull() == null)
    }

    @Test
    fun `getInvitesByEmail should return invites for email`() = runCoroutineTest {
        // Arrange
        val organizationId = createTestOrganization("org_$test_prefix", "")
        val email = "${test_prefix}_byemail_invite@test.com"
        val expiration = clock.now() + 5.minutes
        userDatastore.recordInvite(
            email = email,
            organizationId = organizationId,
            expiration = expiration,
            role = UserRole.USER,
        ).registerInviteForDeletion()

        // Act
        val result = userDatastore.getInvitesByEmail(email)

        // Assert
        assertTrue(result.isSuccess)
        val invites = result.getOrThrow()
        assertTrue(invites.isNotEmpty())
        assertTrue(invites.all { it.email == email })
    }

    @Test
    fun `getInvitesByEmail should return empty list for non-existent email`() = runCoroutineTest {
        // Arrange
        val email = "${test_prefix}_nonexistent_invite@test.com"

        // Act
        val result = userDatastore.getInvitesByEmail(email)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `getInvitesByEmail should not return expired invites`() = runCoroutineTest {
        // Arrange
        val organizationId = createTestOrganization("org_$test_prefix", "")
        val email = "${test_prefix}_expired_invite@test.com"
        val expiration = clock.now() + 2.minutes
        userDatastore.recordInvite(
            email = email,
            organizationId = organizationId,
            expiration = expiration,
            role = UserRole.USER,
        ).registerInviteForDeletion()

        // Act: Move time forward past expiration
        testTimeSource += 5.minutes
        val result = userDatastore.getInvitesByEmail(email)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `removeInvite should delete invite`() = runCoroutineTest {
        // Arrange
        val organizationId = createTestOrganization("org_$test_prefix", "")
        val email = "${test_prefix}_remove_invite@test.com"
        val expiration = clock.now() + 5.minutes
        val recordResult = userDatastore.recordInvite(
            email = email,
            organizationId = organizationId,
            expiration = expiration,
            role = UserRole.USER,
        )
        val createdInvite = recordResult.getOrThrow()

        // Act
        val removeResult = userDatastore.removeInvite(createdInvite.id)

        // Assert
        assertTrue(removeResult.isSuccess)

        // Verify invite is deleted
        val getResult = userDatastore.getInvite(createdInvite.id)
        assertTrue(getResult.isSuccess)
        assertTrue(getResult.getOrNull() == null)

        // Clean up
        userDatastore.purgeInvite(createdInvite.id)
    }

    @Test
    fun `removeInvite should fail for non-existent invite`() = runCoroutineTest {
        // Arrange - Use valid UUID format that doesn't exist in database
        val nonExistentId = InviteId("00000000-0000-0000-0000-000000000000")

        // Act
        val removeResult = userDatastore.removeInvite(nonExistentId)

        // Assert
        assertTrue(removeResult.isFailure)
        assertInstanceOf<ClientRequestExceptions.NotFoundException>(removeResult.exceptionOrNull())
    }
}