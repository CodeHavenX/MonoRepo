package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
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
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SupabaseUserDatastoreIntegrationTest : SupabaseIntegrationTest() {
    private val auth: Auth by inject()

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
            email = "${test_prefix}_user@gmail.com",
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
                email = "${test_prefix}_user@gmail.com",
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
            email = "${test_prefix}_user@gmail.com",
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
                email = "${test_prefix}_user@gmail.com",
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
            email = "${test_prefix}_dupe@gmail.com",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
            isTransient = false,
        ).registerUserForDeletion()
        assertTrue(first.isSuccess)
        val second = userDatastore.createUser(
            email = "${test_prefix}_dupe@gmail.com",
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
            email = "${test_prefix}_getuser@gmail.com",
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
        assertTrue(fetched != null && fetched.email == "${test_prefix}_getuser@gmail.com")
    }

    /**
     * Tests that getUser(email) returns the created user when the email exists.
     */
    @Test
    fun `getUser by email should return created user`() = runCoroutineTest {
        // Arrange
        val email = "${test_prefix}_byemail@gmail.com"

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
        val email = "${test_prefix}_noexist@gmail.com"

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
            email = "${test_prefix}_delete@gmail.com",
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
            email = "${test_prefix}_notfound@gmail.com",
        )

        // Assert
        assertTrue(associateResult.isFailure)
        assertInstanceOf<ClientRequestExceptions.NotFoundException>(associateResult.exceptionOrNull())
    }

    /**
     * Tests that requestPasswordReset succeeds for a registered user's email.
     * Supabase will silently trigger the reset flow without throwing.
     */
    @Test
    fun `requestPasswordReset should succeed for registered email`() = runCoroutineTest {
        // Arrange
        val email = "${test_prefix}_pwreset@gmail.com"
        userDatastore.createUser(
            email = email,
            phoneNumber = "123-456-7890",
            password = "Password1!",
            firstName = "Reset",
            lastName = "User",
            isTransient = false,
        ).registerUserForDeletion()

        // Act
        val result = userDatastore.requestPasswordReset(email, null)

        // Assert
        assertTrue(result.isSuccess)
    }

    /**
     * Tests that requestPasswordReset succeeds even for an unregistered email.
     * Supabase never reveals whether the email exists to prevent enumeration.
     */
    @Test
    fun `requestPasswordReset should succeed for unregistered email`() = runCoroutineTest {
        // Arrange — no user created
        val email = "${test_prefix}_nonexist@gmail.com"

        // Act
        val result = userDatastore.requestPasswordReset(email, null)

        // Assert
        assertTrue(result.isSuccess)
    }

    // We cannot test this in the integration test because it requires a Supabase user to be created first.
    // Right now we can create the user but we are not able to retrieve the user ID from Supabase Auth.
    @Ignore
    @Test
    fun `associateUser should associate a pending user with a Supabase user`() = runCoroutineTest {
        // Arrange: Create a user with a password (Supabase user)
        val email = "${test_prefix}@gmail.com"
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
        // The user ID should now be the Supabase user ID
        assertEquals(UserId(supabaseUserId), associatedUser.id)
        assertEquals(createdUser.email, associatedUser.email)
        assertEquals(createdUser.phoneNumber, associatedUser.phoneNumber)
        assertEquals(createdUser.firstName, associatedUser.firstName)
        assertEquals(createdUser.lastName, associatedUser.lastName)
    }

}
