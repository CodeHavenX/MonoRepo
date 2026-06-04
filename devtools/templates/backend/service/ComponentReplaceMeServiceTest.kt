package com.cramsan.templatereplaceme.server.service

import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.templatereplaceme.lib.model.ComponentReplaceMeId
import com.cramsan.templatereplaceme.server.datastore.ComponentReplaceMeDatastore
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceMe
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for [ComponentReplaceMeService].
 *
 * TODO: Add tests for all business-logic branches (validation, error propagation, etc.).
 */
class ComponentReplaceMeServiceTest : CoroutineTest() {
    private lateinit var datastore: ComponentReplaceMeDatastore
    private lateinit var componentreplacemeService: ComponentReplaceMeService

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        datastore = mockk()
        componentreplacemeService = ComponentReplaceMeService(datastore)
    }

    @Test
    fun `create returns success with the given id`() =
        runCoroutineTest {
            coEvery { datastore.create(any()) } returns
                Result.success(ComponentReplaceMe(id = ComponentReplaceMeId("test-id")))

            val result = componentreplacemeService.create("test-id")

            assertTrue(result.isSuccess)
            val entity = result.getOrThrow()
            assertNotNull(entity.id)
        }
}
