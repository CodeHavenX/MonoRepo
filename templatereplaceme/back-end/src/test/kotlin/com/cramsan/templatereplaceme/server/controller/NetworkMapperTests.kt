package com.cramsan.templatereplaceme.server.controller

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.templatereplaceme.lib.model.UserId
import com.cramsan.templatereplaceme.server.service.models.User
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

@OptIn(NetworkModel::class)
class NetworkMapperTests {

    @Test
    fun `test user network response`() {
        // Arrange
        val user = User(
            id = UserId("user123"),
            firstName = "John",
            lastName = "Doe",
        )

        // Act
        val networkResponse = user.toUserNetworkResponse()

        // Assert
        assertEquals("user123", networkResponse.id)
        assertEquals("John", networkResponse.firstName)
        assertEquals("Doe", networkResponse.lastName)
    }
}
