package com.cramsan.templatereplaceme.server.controller

import com.cramsan.templatereplaceme.lib.model.PingPong
import com.cramsan.templatereplaceme.server.service.models.Pong
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NetworkMapperTests {
    @Test
    fun `test pong network response`() {
        // Arrange
        val pong =
            Pong(
                id = PingPong("user123"),
                firstName = "John",
                lastName = "Doe",
            )

        // Act
        val networkResponse = pong.toPongNetworkResponse()

        // Assert
        assertEquals("user123", networkResponse.id)
        assertEquals("John", networkResponse.firstName)
        assertEquals("Doe", networkResponse.lastName)
    }
}
