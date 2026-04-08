package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for removing a member from an organization.
 * The target organization is specified as a path parameter on the API endpoint.
 */
@NetworkModel
@Serializable
data class RemoveMemberNetworkRequest(
    @SerialName("user_id")
    val userId: UserId,
) : RequestBody
