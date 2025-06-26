package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.EnrollmentType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.EnrollUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
import com.cramsan.framework.utils.uuid.UUID
import io.github.jan.supabase.auth.admin.AdminApi
import org.junit.jupiter.api.assertInstanceOf
import org.koin.test.inject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SupabaseUserDatabaseIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var test_prefix: String

    private val adminApi: AdminApi by inject()

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
    }

    @Test
    fun `createUser should return user on success`() = runBlockingTest {
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
    fun `createUser should fail with existing email`() = runBlockingTest {
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
    fun `createUser should fail with invalid email`() = runBlockingTest {
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
    fun `getUser should return created user`() = runBlockingTest {
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
    fun `deleteUser should remove user`() = runBlockingTest {
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
    fun `deleteUser should fail for non-existent user`() = runBlockingTest {
        // Arrange
        val fakeId = UserId("fake-${test_prefix}")

        // Act
        val deleteResult = userDatabase.deleteUser(DeleteUserRequest(fakeId))

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }

    @Test
    fun `enrollUser should return user on success`() = runBlockingTest {
        // Arrange
        val email = "enrollment-${test_prefix}-user@gmail.com"
        val supabaseUser = adminApi.createUserWithEmail {
            this.email = email
            password = test_prefix
            autoConfirm = true
        }
        val request = EnrollUserRequest(
            userId = UserId(supabaseUser.id),
            enrollmentIdentifier = email,
            enrollmentType = EnrollmentType.EMAIL,
        )

        // Act
        val result = userDatabase.enrollUser(request).registerUserForDeletion()
        val user = result.getOrThrow()

        // Assert
        assertEquals(user.id, UserId(supabaseUser.id))
        assertEquals(user.email, email)
        assertEquals(user.phoneNumber, "")
        assertEquals(user.firstName, "")
        assertEquals(user.lastName, "")
    }
}