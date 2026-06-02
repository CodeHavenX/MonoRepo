package com.cramsan.templatereplaceme.server.service

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.templatereplaceme.lib.model.PingPong
import com.cramsan.templatereplaceme.server.datastore.PingPongDatastore
import com.cramsan.templatereplaceme.server.service.models.Pong
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
 * Test class for [PingPongService].
 */
@OptIn(ExperimentalTime::class)
class PingPongServiceTest {
    private lateinit var pingPongDatastore: PingPongDatastore
    private lateinit var pingPongService: PingPongService

    private lateinit var settingsHolder: SettingsHolder

    /**
     * Sets up the test environment by initializing mocks for [PingPongDatastore] and [pingPongService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        pingPongDatastore = mockk()
        settingsHolder = mockk()
        pingPongService = PingPongService(pingPongDatastore, settingsHolder)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that ping returns a pong.
     */
    @Test
    fun `ping should return a pong`() =
        runTest {
            // Arrange
            val firstName = "John"
            val lastName = "Doe"
            val pong =
                Pong(
                    id = PingPong("user123"),
                    firstName = "John",
                    lastName = "Doe",
                )
            coEvery {
                pingPongDatastore.ping(
                    any(),
                    any(),
                )
            } returns Result.success(pong)
            coEvery {
                settingsHolder.getBoolean(any())
            } returns true

            // Act
            val result = pingPongService.ping(firstName, lastName)

            // Assert
            assertTrue(result.isSuccess)
            coVerify {
                pingPongDatastore.ping(
                    "John",
                    "Doe",
                )
            }
        }
}
