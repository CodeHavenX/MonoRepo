package com.cramsan.templatereplaceme.server.controller

import com.cramsan.templatereplaceme.lib.model.ComponentReplaceMeId
import com.cramsan.templatereplaceme.server.service.models.ComponentReplaceMe
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Tests for [NetworkMapper] extension functions.
 */
class NetworkMapperTests {
    @Test
    fun `test componentreplaceme network response`() {
        // Arrange
        val entity = ComponentReplaceMe(id = ComponentReplaceMeId("test-id"))

        // Act
        val networkResponse = entity.toComponentReplaceMeNetworkResponse()

        // Assert
        assertEquals("test-id", networkResponse.id)
    }
}
