package com.cramsan.templatereplaceme.client.lib.service.impl

import com.cramsan.templatereplaceme.lib.model.PingPong
import com.cramsan.templatereplaceme.lib.model.network.PongNetworkResponse
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [PongNetworkResponse.toPongModel].
 */
class NetworkMapperTest {

    @Test
    fun `toPongModel maps id correctly`() {
        val response = PongNetworkResponse(id = "id-123", firstName = "John", lastName = "Doe")

        val model = response.toPongModel()

        assertEquals(PingPong("id-123"), model.id)
    }

    @Test
    fun `toPongModel maps firstName correctly`() {
        val response = PongNetworkResponse(id = "id-1", firstName = "Alice", lastName = "Smith")

        val model = response.toPongModel()

        assertEquals("Alice", model.firstName)
    }

    @Test
    fun `toPongModel maps lastName correctly`() {
        val response = PongNetworkResponse(id = "id-1", firstName = "Alice", lastName = "Smith")

        val model = response.toPongModel()

        assertEquals("Smith", model.lastName)
    }

    @Test
    fun `toPongModel maps all fields correctly`() {
        val response = PongNetworkResponse(id = "abc-99", firstName = "Bob", lastName = "Jones")

        val model = response.toPongModel()

        assertEquals(PingPong("abc-99"), model.id)
        assertEquals("Bob", model.firstName)
        assertEquals("Jones", model.lastName)
    }
}
