package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.user.UserId
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
