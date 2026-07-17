package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.edifikana.lib.model.common.Email
import com.cramsan.edifikana.lib.model.organization.OrgMemberStatus
import com.cramsan.edifikana.lib.model.organization.OrgRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Response model for a single organization member.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A member of an organization.")
data class MemberNetworkResponse(
    @SerialName("user_id")
    @JsonSchema.Description("Identifier of the member's user account.")
    val userId: UserId,
    @SerialName("org_id")
    @JsonSchema.Description("Identifier of the organization.")
    val orgId: OrganizationId,
    @SerialName("role")
    @JsonSchema.Description("Role of the member within the organization.")
    val role: OrgRole,
    @SerialName("status")
    @JsonSchema.Description("Current membership status of the member.")
    val status: OrgMemberStatus,
    @SerialName("joined_at")
    @JsonSchema.Description("ISO-8601 timestamp when the member joined, or null if not yet confirmed.")
    @JsonSchema.Format("date-time")
    val joinedAt: Instant?,
    @SerialName("email")
    @JsonSchema.Description("Email address of the member.")
    val email: Email,
    @SerialName("display_name")
    @JsonSchema.Description("Display name of the member.")
    val displayName: String,
) : ResponseBody
