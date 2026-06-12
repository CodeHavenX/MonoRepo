package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.templatereplaceme.lib.model.ComponentReplaceMeId
import com.cramsan.templatereplaceme.lib.model.network.ComponentReplaceMeNetworkResponse
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [ComponentReplaceMeNetworkResponse.toComponentReplaceMeModel].
 */
class NetworkMapperTest {

    @Test
    fun `toComponentReplaceMeModel maps id correctly`() {
        val response = ComponentReplaceMeNetworkResponse(id = "id-123")

        val model = response.toComponentReplaceMeModel()

        assertEquals(ComponentReplaceMeId("id-123"), model.id)
    }
}
