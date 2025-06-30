package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.repository.UserDatabase
import com.cramsan.edifikana.server.core.service.models.User
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
    private lateinit var userDatabase: UserDatabase
    private lateinit var userService: UserService

    /**
     * Sets up the test environment by initializing mocks for [UserDatabase] and [userService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        userDatabase = mockk()
        userService = UserService(userDatabase)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that createUser creates a user and sends an OTP if authorizeOtp is true.
     */
    @Test
    fun `createUser should create user and send OTP if authorizeOtp is true`() = runTest {
        // Arrange
        val email = "test@example.com"
        val phone = "1234567890"
        val password = ""
        val firstName = "John"
        val lastName = "Doe"
        val user = mockk<User>()
        coEvery { userDatabase.createUser(any()) } returns Result.success(user)
        coEvery { userDatabase.sendOtpCode(email) } returns Result.success(Unit)

        // Act
        val result = userService.createUser(email, phone, password, firstName, lastName, true)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { userDatabase.createUser(match { it.email == email }) }
        coVerify { userDatabase.sendOtpCode(email) }
    }

    /**
     * Tests that createUser does not send an OTP if authorizeOtp is false.
     */
    @Test
    fun `createUser should not send OTP if authorizeOtp is false`() = runTest {
        // Arrange
        val email = "test@example.com"
        val user = mockk<User>()
        coEvery { userDatabase.createUser(any()) } returns Result.success(user)

        // Act
        userService.createUser(email, "123", null, "A", "B", false)

        // Assert
        coVerify(exactly = 0) { userDatabase.sendOtpCode(email) }
    }

    /**
     * Tests that getUser retrieves a user from the database.
     */
    @Test
    fun `getUser should return user from database`() = runTest {
        // Arrange
        val userId = UserId("id")
        val user = mockk<User>()
        coEvery { userDatabase.getUser(any()) } returns Result.success(user)

        // Act
        val result = userService.getUser(userId)

        // Assert
        assertEquals(user, result)
        coVerify { userDatabase.getUser(match { it.id == userId }) }
    }

    /**
     * Tests that getUsers retrieves all users from the database.
     */
    @Test
    fun `getUsers should return all users`() = runTest {
        // Arrange
        val users = listOf(mockk<User>(), mockk())
        coEvery { userDatabase.getUsers() } returns Result.success(users)

        // Act
        val result = userService.getUsers()

        // Assert
        assertEquals(users, result)
        coVerify { userDatabase.getUsers() }
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
        coEvery { userDatabase.updateUser(any()) } returns Result.success(user)

        // Act
        val result = userService.updateUser(userId, email)

        // Assert
        assertEquals(user, result)
        coVerify { userDatabase.updateUser(match { it.id == userId && it.email == email }) }
    }

    /**
     * Tests that deleteUser deletes a user and returns the result.
     */
    @Test
    fun `deleteUser should delete user and return result`() = runTest {
        // Arrange
        val userId = UserId("id")
        coEvery { userDatabase.deleteUser(any()) } returns Result.success(true)

        // Act
        val result = userService.deleteUser(userId)

        // Assert
        assertTrue(result)
        coVerify { userDatabase.deleteUser(match { it.id == userId }) }
    }

    /**
     * Tests that updatePassword updates a user's password and returns the result.
     */
    @Test
    fun `updatePassword should update password and return result`() = runTest {
        // Arrange
        val userId = UserId("id")
        val password = "newpass"
        coEvery { userDatabase.updatePassword(any()) } returns Result.success(true)

        // Act
        val result = userService.updatePassword(userId, password)

        // Assert
        assertTrue(result)
        coVerify { userDatabase.updatePassword(match { it.id == userId && it.password == password }) }
    }
}
