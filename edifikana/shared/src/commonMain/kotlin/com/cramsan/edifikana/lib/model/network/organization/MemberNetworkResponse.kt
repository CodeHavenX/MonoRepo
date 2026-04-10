package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.edifikana.lib.model.organization.OrgMemberStatus
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Response model for a single organization member.
 */
@NetworkModel
@Serializable
data class MemberNetworkResponse(
    @SerialName("user_id")
    val userId: UserId,
    @SerialName("org_id")
    val orgId: OrganizationId,
    @SerialName("role")
    val role: OrgRole,
    @SerialName("status")
    val status: OrgMemberStatus,
    @SerialName("joined_at")
    val joinedAt: Instant?,
    @SerialName("email")
    val email: String,
    @SerialName("display_name")
    val displayName: String,
) : ResponseBody
