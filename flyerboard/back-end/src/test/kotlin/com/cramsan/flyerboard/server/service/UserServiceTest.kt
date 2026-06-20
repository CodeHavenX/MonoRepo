package com.cramsan.flyerboard.server.service

import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.server.datastore.UserDatastore
import com.cramsan.flyerboard.server.datastore.UserProfileDatastore
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.flyerboard.server.service.models.UserProfile
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.assertFailsWith
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Test class for [UserService].
 */
@OptIn(ExperimentalTime::class)
class UserServiceTest {
    private lateinit var userDatastore: UserDatastore
    private lateinit var userProfileDatastore: UserProfileDatastore
    private lateinit var userService: UserService

    /**
     * Sets up the test environment by initializing mocks for [UserDatastore] and [userService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        userDatastore = mockk()
        userProfileDatastore = mockk()
        userService = UserService(userDatastore, userProfileDatastore)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that createUser creates a user and a default profile for them.
     */
    @Test
    fun `createUser should create user and profile`() =
        runTest {
            // Arrange
            val firstName = "John"
            val lastName = "Doe"
            val user =
                User(
                    id = UserId("user123"),
                    firstName = "John",
                    lastName = "Doe",
                )
            val profile =
                UserProfile(
                    id = UserId("user123"),
                    role = UserRole.USER,
                    createdAt = Instant.fromEpochSeconds(0),
                    updatedAt = Instant.fromEpochSeconds(0),
                )
            coEvery {
                userDatastore.createUser(
                    any(),
                    any(),
                    any(),
                )
            } returns Result.success(user)
            coEvery {
                userProfileDatastore.createUserProfile(UserId("user123"), UserRole.USER)
            } returns Result.success(profile)

            // Act
            val result = userService.createUser(UserId("user123"), firstName, lastName)

            // Assert
            assertEquals(user, result)
            coVerify {
                userDatastore.createUser(
                    UserId("user123"),
                    "John",
                    "Doe",
                )
                userProfileDatastore.createUserProfile(UserId("user123"), UserRole.USER)
            }
        }

    /**
     * Tests that createUser throws when the datastore fails to create the user, without
     * attempting to create a profile.
     */
    @Test
    fun `createUser throws when datastore fails`() =
        runTest {
            // Arrange
            val firstName = "John"
            val lastName = "Doe"
            val exception = IllegalStateException("datastore error")
            coEvery {
                userDatastore.createUser(
                    any(),
                    any(),
                    any(),
                )
            } returns Result.failure(exception)

            // Act
            val thrown =
                assertFailsWith<IllegalStateException> {
                    userService.createUser(UserId("user123"), firstName, lastName)
                }

            // Assert
            assertEquals(exception, thrown)
            coVerify(exactly = 0) {
                userProfileDatastore.createUserProfile(any(), any())
            }
        }

    /**
     * Tests that createUser throws when the profile datastore fails to create the profile.
     */
    @Test
    fun `createUser throws when profile creation fails`() =
        runTest {
            // Arrange
            val firstName = "John"
            val lastName = "Doe"
            val user =
                User(
                    id = UserId("user123"),
                    firstName = "John",
                    lastName = "Doe",
                )
            val exception = IllegalStateException("profile datastore error")
            coEvery {
                userDatastore.createUser(
                    any(),
                    any(),
                    any(),
                )
            } returns Result.success(user)
            coEvery {
                userProfileDatastore.createUserProfile(UserId("user123"), UserRole.USER)
            } returns Result.failure(exception)

            // Act
            val thrown =
                assertFailsWith<IllegalStateException> {
                    userService.createUser(UserId("user123"), firstName, lastName)
                }

            // Assert
            assertEquals(exception, thrown)
        }

    /**
     * Tests that getUser returns the user from the datastore.
     */
    @Test
    fun `getUser returns user from datastore`() =
        runTest {
            // Arrange
            val user =
                User(
                    id = UserId("user123"),
                    firstName = "John",
                    lastName = "Doe",
                )
            coEvery { userDatastore.getUser(UserId("user123")) } returns Result.success(user)

            // Act
            val result = userService.getUser(UserId("user123"))

            // Assert
            assertEquals(user, result)
            coVerify { userDatastore.getUser(UserId("user123")) }
        }

    /**
     * Tests that getUser throws when the datastore fails to find the user.
     */
    @Test
    fun `getUser throws when datastore fails`() =
        runTest {
            // Arrange
            val exception = IllegalStateException("datastore error")
            coEvery { userDatastore.getUser(UserId("user123")) } returns Result.failure(exception)

            // Act
            val thrown =
                assertFailsWith<IllegalStateException> {
                    userService.getUser(UserId("user123"))
                }

            // Assert
            assertEquals(exception, thrown)
        }
}
