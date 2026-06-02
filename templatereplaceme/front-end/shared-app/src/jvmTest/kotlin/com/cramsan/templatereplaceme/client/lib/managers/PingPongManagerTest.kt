package com.cramsan.templatereplaceme.client.lib.managers

import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.templatereplaceme.client.lib.models.PongModel
import com.cramsan.templatereplaceme.client.lib.service.PingPongService
import com.cramsan.templatereplaceme.lib.model.PingPong
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [PingPongManager].
 */
class PingPongManagerTest : CoroutineTest() {

    private lateinit var manager: PingPongManager
    private lateinit var pingPongService: PingPongService
    private lateinit var dependencies: ManagerDependencies

    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        pingPongService = mockk()
        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)
        manager = PingPongManager(dependencies, pingPongService)
    }

    @Test
    fun `ping returns success when service succeeds`() = runCoroutineTest {
        val pong = PongModel(id = PingPong("id-1"), firstName = "John", lastName = "Doe")
        coEvery { pingPongService.ping("John", "Doe") } returns Result.success(pong)

        val result = manager.ping("John", "Doe")

        assertTrue(result.isSuccess)
        assertEquals(pong, result.getOrNull())
        coVerify { pingPongService.ping("John", "Doe") }
    }
}
