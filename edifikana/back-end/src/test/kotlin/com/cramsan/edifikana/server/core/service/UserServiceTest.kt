package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.datastore.UserDatastore
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
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

/**
 * Test class for [UserService].
 */
class UserServiceTest {
    private lateinit var userDatastore: UserDatastore
    private lateinit var userService: UserService

    /**
     * Sets up the test environment by initializing mocks for [UserDatastore] and [userService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        userDatastore = mockk()
        userService = UserService(userDatastore)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that createUser creates a user.
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
        coEvery { userDatastore.createUser(any()) } returns Result.success(user)

        // Act
        val result = userService.createUser(email, phone, password, firstName, lastName)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { userDatastore.createUser(match { it.email == email }) }
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
        coVerify { userDatastore.getUser(match { it.id == userId }) }
    }

    /**
     * Tests that getUsers retrieves all users from the database.
     */
    @Test
    fun `getUsers should return all users`() = runTest {
        // Arrange
        val users = listOf(mockk<User>(), mockk())
        coEvery { userDatastore.getUsers() } returns Result.success(users)

        // Act
        val result = userService.getUsers()

        // Assert
        assertEquals(Result.success(users), result)
        coVerify { userDatastore.getUsers() }
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
        coEvery { userDatastore.updateUser(any()) } returns Result.success(user)

        // Act
        val result = userService.updateUser(userId, email)

        // Assert
        assertEquals(Result.success(user), result)
        coVerify { userDatastore.updateUser(match { it.id == userId && it.email == email }) }
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
        coVerify { userDatastore.deleteUser(match { it.id == userId }) }
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
        coEvery { userDatastore.updatePassword(any()) } returns Result.success(Unit)

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
                match {
                    it.id == userId &&
                        it.newPassword == password &&
                        it.currentHashedPassword == hashedPassword
                }
            )
        }
    }
}
