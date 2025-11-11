package com.cramsan.templatereplaceme.server.service

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.templatereplaceme.lib.model.UserId
import com.cramsan.templatereplaceme.server.datastore.UserDatastore
import com.cramsan.templatereplaceme.server.service.models.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.time.ExperimentalTime

/**
 * Test class for [UserService].
 */
@OptIn(ExperimentalTime::class)
class UserServiceTest {
    private lateinit var userDatastore: UserDatastore
    private lateinit var userService: UserService

    private lateinit var settingsHolder: SettingsHolder

    /**
     * Sets up the test environment by initializing mocks for [UserDatastore] and [userService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        userDatastore = mockk()
        settingsHolder = mockk()
        userService = UserService(userDatastore, settingsHolder)
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
        val firstName = "John"
        val lastName = "Doe"
        val user = User(
            id = UserId("user123"),
            firstName = "John",
            lastName = "Doe",
        )
        coEvery {
            userDatastore.createUser(
                any(),
                any(),
            )
        } returns Result.success(user)
        coEvery {
            settingsHolder.getBoolean(any())
        } returns true

        // Act
        val result = userService.createUser(firstName, lastName)

        // Assert
        assertTrue(result.isSuccess)
        coVerify {
            userDatastore.createUser(
                "John",
                "Doe",
            )
        }
    }
}
