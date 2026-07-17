package com.cramsan.edifikana.lib.model.network.invite

import com.cramsan.edifikana.lib.model.common.Email
import com.cramsan.edifikana.lib.model.invite.InviteCode
import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.invite.InviteRole
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Response model for an invite.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("An invite to join an organization.")
data class InviteNetworkResponse(
    @SerialName("invite_id")
    @JsonSchema.Description("Unique identifier of the invite.")
    val inviteId: InviteId,
    @SerialName("email")
    @JsonSchema.Description("Email address the invite was sent to.")
    val email: Email,
    @SerialName("organization_id")
    @JsonSchema.Description("Identifier of the organization the invite is for.")
    val organizationId: OrganizationId,
    @SerialName("role")
    @JsonSchema.Description("Role the invitee will be granted upon accepting.")
    val role: InviteRole,
    @SerialName("expires_at")
    @JsonSchema.Description("ISO-8601 timestamp after which the invite is no longer valid.")
    @JsonSchema.Format("date-time")
    val expiresAt: Instant,
    @SerialName("invite_code")
    @JsonSchema.Description("Code the invitee can use to join, or null if not included in this response.")
    val inviteCode: InviteCode? = null,
    @SerialName("created_at")
    @JsonSchema.Description("ISO-8601 timestamp when the invite was created, or null if unknown.")
    @JsonSchema.Format("date-time")
    val createdAt: Instant? = null,
) : ResponseBody
