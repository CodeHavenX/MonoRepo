package com.cramsan.templatereplaceme.server

import com.cramsan.architecture.server.test.dependencyinjection.TestArchitectureModule
import com.cramsan.architecture.server.test.dependencyinjection.integTestFrameworkModule
import com.cramsan.framework.test.CoroutineTest
import com.cramsan.templatereplaceme.server.datastore.impl.ExamplePingPongDatastore
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
 * Integration tests for the ping-pong datastore layer.
 *
 * These tests exercise [ExamplePingPongDatastore] directly. When migrating to a real backend,
 * swap [ExamplePingPongDatastore] for a real datastore implementation and load its module in
 * the [setUp] Koin context.
 */
class PingPongDatastoreIntegrationTest : CoroutineTest(), KoinTest {

    private val datastore = ExamplePingPongDatastore()

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
    fun `ping returns success with the provided names`() = runCoroutineTest {
        // Arrange

        // Act
        val result = datastore.ping(firstName = "John", lastName = "Doe")

        // Assert
        assertTrue(result.isSuccess)
        val pong = result.getOrNull()
        assertNotNull(pong)
        assertEquals("John", pong.firstName)
        assertEquals("Doe", pong.lastName)
        assertNotNull(pong.id)
    }
}
