package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.AssociateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
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
        val request = CreateUserRequest(
            email = "${test_prefix}_user@test.com",
            phoneNumber = "123-456-7890",
            password = "Password1!",
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
        )

        // Act
        val result = userDatastore.createUser(request).registerUserForDeletion()

        // Assert
        assertEquals(
            User(
                id = result.getOrThrow().id,
                email = request.email,
                phoneNumber = request.phoneNumber,
                firstName = request.firstName,
                lastName = request.lastName,
                isVerified = false,
            ),
            result.getOrNull(),
        )
    }

    @Test
    fun `createUser with null password should succeed with a temp user pending association`() = runCoroutineTest {
        // Arrange
        val request = CreateUserRequest(
            email = "${test_prefix}_user@test.com",
            phoneNumber = "123-456-7890",
            password = null, // No password provided
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
        )

        // Act
        val result = userDatastore.createUser(request).registerUserForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrThrow()
        assertEquals(
            User(
                id = user.id,
                email = request.email,
                phoneNumber = request.phoneNumber,
                firstName = request.firstName,
                lastName = request.lastName,
                isVerified = false,
            ),
            user,
        )
    }

    @Test
    fun `createUser should fail with existing email`() = runCoroutineTest {
        // Arrange
        val request = CreateUserRequest(
            email = "${test_prefix}_dupe@test.com",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
        )

        // Act
        val first = userDatastore.createUser(request).registerUserForDeletion()
        assertTrue(first.isSuccess)
        val second = userDatastore.createUser(request)

        // Assert
        assertTrue(second.isFailure)
        assertInstanceOf<ClientRequestExceptions.ConflictException>(second.exceptionOrNull())
    }

    @Test
    fun `createUser should fail with invalid email`() = runCoroutineTest {
        // Arrange
        val request = CreateUserRequest(
            email = "not-an-email",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "Invalid",
            lastName = "User",
        )

        // Act
        val result = userDatastore.createUser(request)

        // Assert
        assertTrue(result.isFailure)
        assertInstanceOf<AuthRestException>(result.exceptionOrNull())
    }

    @Test
    fun `getUser should return created user`() = runCoroutineTest {
        // Arrange
        val request = CreateUserRequest(
            email = "${test_prefix}_getuser@test.com",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "Get",
            lastName = "User",
        )

        // Act
        val createResult = userDatastore.createUser(request).registerUserForDeletion()
        assertTrue(createResult.isSuccess)
        val user = createResult.getOrNull()!!
        val getResult = userDatastore.getUser(GetUserRequest(user.id))

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertTrue(fetched != null && fetched.email == request.email)
    }

    @Test
    fun `deleteUser should remove user`() = runCoroutineTest {
        // Arrange
        val request = CreateUserRequest(
            email = "${test_prefix}_delete@test.com",
            phoneNumber = "123-456-7890",
            password = "password",
            firstName = "Delete",
            lastName = "User",
        )

        // Act
        val createResult = userDatastore.createUser(request)
        assertTrue(createResult.isSuccess)
        val user = createResult.getOrNull()!!
        val deleteResult = userDatastore.deleteUser(DeleteUserRequest(user.id))

        // Assert
        assertTrue(deleteResult.isSuccess && deleteResult.getOrNull() == true)
        val getResult = userDatastore.getUser(GetUserRequest(user.id))
        assertTrue(getResult.isSuccess && getResult.getOrNull() == null)
    }

    @Test
    fun `deleteUser should fail for non-existent user`() = runCoroutineTest {
        // Arrange
        val fakeId = UserId(test_prefix)

        // Act
        val deleteResult = userDatastore.deleteUser(DeleteUserRequest(fakeId))

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
        assertInstanceOf<ClientRequestExceptions.NotFoundException>(deleteResult.exceptionOrNull())
    }

    @Test
    fun `associateUser should fail for non-existent Supabase user`() = runCoroutineTest {
        // Arrange: Use an email that does not exist
        val associateRequest = AssociateUserRequest(
            userId = UserId(test_prefix),
            email = "${test_prefix}_notfound@test.com",
        )

        // Act
        val associateResult = userDatastore.associateUser(associateRequest)

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
        val createRequest = CreateUserRequest(
            email = email,
            phoneNumber = "123-456-7890",
            password = null,
            firstName = "Associate",
            lastName = "User",
        )
        val createResult = userDatastore.createUser(createRequest).registerUserForDeletion()
        val createdUser = createResult.getOrNull()!!
        auth.signInWith(OTP) {
            this.email = email
            createUser = true // This will create a user in Supabase Auth.
        }
        val supabaseUserId = auth.currentUserOrNull()!!.id
        registerSupabaseUserForDeletion(supabaseUserId)

        // Act: Try to associate the created Supabase user with the pending user's email
        val associateRequest = AssociateUserRequest(
            userId = UserId(supabaseUserId),
            email = email,
        )
        val associateResult = userDatastore.associateUser(associateRequest)

        // Assert
        assertTrue(associateResult.isSuccess)
        val associatedUser = associateResult.getOrThrow()
        assertEquals(createdUser.id, associatedUser.id)
        assertEquals(createdUser.email, associatedUser.email)
        assertEquals(createdUser.phoneNumber, associatedUser.phoneNumber)
        assertEquals(createdUser.firstName, associatedUser.firstName)
        assertEquals(createdUser.lastName, associatedUser.lastName)
    }
}