package com.cramsan.templatereplaceme.server

import com.cramsan.architecture.server.test.dependencyinjection.TestArchitectureModule
import com.cramsan.architecture.server.test.dependencyinjection.integTestFrameworkModule
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.templatereplaceme.server.datastore.impl.ExampleComponentReplaceMeDatastore
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests for the [ComponentReplaceMe] datastore layer.
 *
 * These tests exercise [ExampleComponentReplaceMeDatastore] directly. When migrating to a real
 * backend, swap [ExampleComponentReplaceMeDatastore] for the real implementation and load its
 * module in the [setUp] Koin context.
 */
class ComponentReplaceMeDatastoreIntegrationTest : CoroutineTest(), KoinTest {

    private val datastore = ExampleComponentReplaceMeDatastore()

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(
                TestArchitectureModule,
                integTestFrameworkModule("TEMPLATE_REPLACE_ME"),
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `create returns success with the provided id`() = runCoroutineTest {
        // Act
        val result = datastore.create(id = "test-id")

        // Assert
        assertTrue(result.isSuccess)
        val entity = result.getOrNull()
        assertNotNull(entity)
        assertEquals("test-id", entity.id.id)
    }
}
