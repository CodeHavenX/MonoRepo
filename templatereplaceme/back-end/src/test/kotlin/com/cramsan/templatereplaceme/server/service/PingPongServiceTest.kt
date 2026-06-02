package com.cramsan.templatereplaceme.server.service

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.templatereplaceme.server.datastore.impl.ExamplePingPongDatastore
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Test class for [PingPongService].
 */
class PingPongServiceTest : CoroutineTest() {
    private lateinit var pingPongService: PingPongService
    private lateinit var settingsHolder: SettingsHolder

    /**
     * Sets up the test environment using [ExamplePingPongDatastore] as the real in-memory datastore.
     */
    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        settingsHolder = mockk(relaxed = true)
        pingPongService = PingPongService(ExamplePingPongDatastore(), settingsHolder)
    }

    /**
     * Tests that ping returns a successful pong with the names passed in.
     */
    @Test
    fun `ping returns success with correct names`() =
        runCoroutineTest {
            val result = pingPongService.ping("John", "Doe")

            assertTrue(result.isSuccess)
            val pong = result.getOrThrow()
            assertEquals("John", pong.firstName)
            assertEquals("Doe", pong.lastName)
            assertNotNull(pong.id)
        }

    /**
     * Tests that ping preserves first and last name values regardless of content.
     */
    @Test
    fun `ping preserves first and last name values`() =
        runCoroutineTest {
            val result = pingPongService.ping("Alice", "Smith")

            assertTrue(result.isSuccess)
            assertEquals("Alice", result.getOrThrow().firstName)
            assertEquals("Smith", result.getOrThrow().lastName)
        }
}
