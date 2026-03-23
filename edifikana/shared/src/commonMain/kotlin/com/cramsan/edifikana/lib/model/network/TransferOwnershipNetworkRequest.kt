package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for transferring organization ownership to another member.
 * The target organization is specified as a path parameter on the API endpoint.
 */
@NetworkModel
@Serializable
data class TransferOwnershipNetworkRequest(
    @SerialName("new_owner_id")
    val newOwnerId: UserId,
) : RequestBody
