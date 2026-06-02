package com.cramsan.templatereplaceme.server.datastore.impl

import com.cramsan.framework.test.CoroutineTest
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PingPongDatastoreImplTest : CoroutineTest() {
    lateinit var userDatastore: ExamplePingPongDatastore

    @BeforeTest
    fun setUp() {
        userDatastore = ExamplePingPongDatastore()
    }

    @Test
    fun `test ping`() =
        runCoroutineTest {
            // Arrange

            // Act
            val user =
                userDatastore.ping(
                    firstName = "John",
                    lastName = "Doe",
                )

            // Assert
            assertTrue(user.isSuccess)
            val createdUser = user.getOrNull()
            assertNotNull(createdUser)
            assertEquals("John", createdUser.firstName)
            assertEquals("Doe", createdUser.lastName)
            assertNotNull(createdUser.id)
        }
}
