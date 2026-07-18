package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for transferring organization ownership to another member.
 * The target organization is specified as a path parameter on the API endpoint.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to transfer organization ownership to another member.")
data class TransferOwnershipNetworkRequest(
    @SerialName("new_owner_id")
    @JsonSchema.Description("Identifier of the member who will become the new owner.")
    val newOwnerId: UserId,
) : RequestBody
