package com.cramsan.templatereplaceme.server.service

import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.templatereplaceme.lib.model.ComponentReplacemeId
import com.cramsan.templatereplaceme.server.datastore.ComponentReplacemeDatastore
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceme
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for [ComponentReplacemeService].
 *
 * TODO: Add tests for all business-logic branches (validation, error propagation, etc.).
 */
class ComponentReplacemeServiceTest : CoroutineTest() {
    private lateinit var datastore: ComponentReplacemeDatastore
    private lateinit var componentreplacemeService: ComponentReplacemeService

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        datastore = mockk()
        componentreplacemeService = ComponentReplacemeService(datastore)
    }

    @Test
    fun `create returns success with the given id`() =
        runCoroutineTest {
            coEvery { datastore.create(any()) } returns
                Result.success(ComponentReplaceme(id = ComponentReplacemeId("test-id")))

            val result = componentreplacemeService.create("test-id")

            assertTrue(result.isSuccess)
            val entity = result.getOrThrow()
            assertNotNull(entity.id)
        }
}
