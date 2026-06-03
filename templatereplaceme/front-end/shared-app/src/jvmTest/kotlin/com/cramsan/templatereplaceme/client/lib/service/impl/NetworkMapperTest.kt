package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.templatereplaceme.lib.model.ComponentReplacemeId
import com.cramsan.templatereplaceme.lib.model.network.ComponentReplacemeNetworkResponse
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [ComponentReplacemeNetworkResponse.toComponentReplacemeModel].
 */
class NetworkMapperTest {

    @Test
    fun `toComponentReplacemeModel maps id correctly`() {
        val response = ComponentReplacemeNetworkResponse(id = "id-123")

        val model = response.toComponentReplacemeModel()

        assertEquals(ComponentReplacemeId("id-123"), model.id)
    }
}
