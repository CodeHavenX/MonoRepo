package com.cramsan.templatereplaceme.server.datastore.impl

import com.cramsan.framework.test.CoroutineTest
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for [ExampleComponentReplacemeDatastore].
 */
class ComponentReplacemeDatastoreImplTest : CoroutineTest() {
    private lateinit var datastore: ExampleComponentReplacemeDatastore

    @BeforeTest
    fun setUp() {
        datastore = ExampleComponentReplacemeDatastore()
    }

    @Test
    fun `create returns success with given id`() =
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
