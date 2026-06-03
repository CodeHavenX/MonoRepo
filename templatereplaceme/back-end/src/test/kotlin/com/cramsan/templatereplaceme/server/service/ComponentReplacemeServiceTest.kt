package com.cramsan.templatereplaceme.server.service

import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.templatereplaceme.server.datastore.impl.ExampleComponentReplacemeDatastore
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for [ComponentReplacemeService].
 *
 * Uses [ExampleComponentReplacemeDatastore] as the in-memory datastore.
 */
class ComponentReplacemeServiceTest : CoroutineTest() {
    private lateinit var componentreplacemeService: ComponentReplacemeService

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        componentreplacemeService = ComponentReplacemeService(ExampleComponentReplacemeDatastore())
    }

    @Test
    fun `create returns success with the given id`() =
        runCoroutineTest {
            val result = componentreplacemeService.create("test-id")

            assertTrue(result.isSuccess)
            val entity = result.getOrThrow()
            assertNotNull(entity.id)
        }
}
