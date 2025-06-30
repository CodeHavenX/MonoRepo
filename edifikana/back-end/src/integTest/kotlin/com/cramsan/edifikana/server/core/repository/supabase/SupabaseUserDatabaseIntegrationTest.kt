package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
import com.cramsan.framework.utils.uuid.UUID
import org.junit.jupiter.api.assertInstanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SupabaseUserDatabaseIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var test_prefix: String

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
    }

    @Test
    fun `createUser should return user on success`() = runCoroutineTest {
        // Arrange
        val request = CreateUserRequest(
            email = "${test_prefix}_user@test.com",
            phoneNumber = "123-456-7890",
            password = "Password1!",
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
        )

        // Act
        val result = userDatabase.createUser(request).registerUserForDeletion()

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
        val first = userDatabase.createUser(request).registerUserForDeletion()
        assertTrue(first.isSuccess)
        val second = userDatabase.createUser(request)

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
        val result = userDatabase.createUser(request)

        // Assert
        assertTrue(result.isFailure)
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
        val createResult = userDatabase.createUser(request).registerUserForDeletion()
        assertTrue(createResult.isSuccess)
        val user = createResult.getOrNull()!!
        val getResult = userDatabase.getUser(GetUserRequest(user.id))

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
        val createResult = userDatabase.createUser(request)
        assertTrue(createResult.isSuccess)
        val user = createResult.getOrNull()!!
        val deleteResult = userDatabase.deleteUser(DeleteUserRequest(user.id))

        // Assert
        assertTrue(deleteResult.isSuccess && deleteResult.getOrNull() == true)
        val getResult = userDatabase.getUser(GetUserRequest(user.id))
        assertTrue(getResult.isSuccess && getResult.getOrNull() == null)
    }

    @Test
    fun `deleteUser should fail for non-existent user`() = runCoroutineTest {
        // Arrange
        val fakeId = UserId("fake-${test_prefix}")

        // Act
        val deleteResult = userDatabase.deleteUser(DeleteUserRequest(fakeId))

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }
}