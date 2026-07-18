package com.cramsan.templatereplaceme.server.datastore.impl

import com.cramsan.framework.test.CoroutineTest
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for [ExampleComponentReplaceMeDatastore].
 *
 * When you replace [ExampleComponentReplaceMeDatastore] with a real implementation,
 * create a matching `<Provider>ComponentReplaceMeDatastoreTest` that follows this same pattern.
 * Integration tests that require a live database should extend this to a separate
 * `integTest` source set.
 *
 * TODO: Add tests for all operations defined in [ComponentReplaceMeDatastore].
 */
class ComponentReplaceMeDatastoreImplTest : CoroutineTest() {
    private lateinit var datastore: ExampleComponentReplaceMeDatastore

    @BeforeTest
    fun setUp() {
        datastore = ExampleComponentReplaceMeDatastore()
    }

    @Test
    fun `create returns success with given id`(): Unit =
        runCoroutineTest {
            // Act
            val result = datastore.create(id = "test-id")

            // Assert
            assertTrue(result.isSuccess)
            val entity = result.getOrNull()
            assertNotNull(entity)
            assertEquals("test-id", entity.id.id)
        }
}
