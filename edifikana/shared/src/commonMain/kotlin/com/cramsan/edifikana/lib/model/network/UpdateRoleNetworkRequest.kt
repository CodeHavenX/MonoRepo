package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.OrgRole
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for updating a member's role within an organization.
 * The target organization is specified as a path parameter on the API endpoint.
 */
@NetworkModel
@Serializable
data class UpdateRoleNetworkRequest(
    @SerialName("user_id")
    val userId: UserId,
    @SerialName("role")
    val role: OrgRole,
) : RequestBody
