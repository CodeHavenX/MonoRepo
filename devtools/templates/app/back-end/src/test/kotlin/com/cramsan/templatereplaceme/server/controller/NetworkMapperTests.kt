package com.cramsan.templatereplaceme.server.controller

import com.cramsan.templatereplaceme.lib.model.ComponentReplacemeId
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceme
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Tests for [NetworkMapper] extension functions.
 */
class NetworkMapperTests {
    @Test
    fun `test componentreplaceme network response`() {
        // Arrange
        val entity = ComponentReplaceme(id = ComponentReplacemeId("test-id"))

        // Act
        val networkResponse = entity.toComponentReplacemeNetworkResponse()

        // Assert
        assertEquals("test-id", networkResponse.id)
    }
}
