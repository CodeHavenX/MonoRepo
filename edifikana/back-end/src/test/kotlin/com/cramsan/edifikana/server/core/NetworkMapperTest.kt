package com.cramsan.edifikana.server.core

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.AuthMetadataNetworkResponse
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.edifikana.server.core.controller.toUserNetworkResponse
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.framework.annotations.NetworkModel
import kotlin.test.Test
import kotlin.test.assertEquals

class NetworkMapperTest {

    @OptIn(NetworkModel::class)
    @Test
    fun `map user to network response`() {
        // Arrange
        val user = User(
            id = UserId("123"),
            email = "test@test.com",
            phoneNumber = "1234567890",
            firstName = "Test",
            lastName = "User",
            authMetadata = User.AuthMetadata(isPasswordSet = true)
        )
        val expectedResponse = UserNetworkResponse(
            id = "123",
            email = "test@test.com",
            phoneNumber = "1234567890",
            firstName = "Test",
            lastName = "User",
            authMetadata = AuthMetadataNetworkResponse(isPasswordSet = true)
        )

        // Act
        val response = user.toUserNetworkResponse()
        // Assert
        assertEquals(expectedResponse, response)
    }
}
