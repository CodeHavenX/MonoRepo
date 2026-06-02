package com.cramsan.templatereplaceme.server.datastore.impl

import com.cramsan.framework.test.CoroutineTest
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PingPongDatastoreImplTest : CoroutineTest() {
    lateinit var pingpongDatastore: ExamplePingPongDatastore

    @BeforeTest
    fun setUp() {
        pingpongDatastore = ExamplePingPongDatastore()
    }

    @Test
    fun `test ping`() =
        runCoroutineTest {
            // Arrange

            // Act
            val pongResponse =
                pingpongDatastore.ping(
                    firstName = "John",
                    lastName = "Doe",
                )

            // Assert
            assertTrue(pongResponse.isSuccess)
            val pong = pongResponse.getOrNull()
            assertNotNull(pong)
            assertEquals("John", pong.firstName)
            assertEquals("Doe", pong.lastName)
            assertNotNull(pong.id)
        }
}
