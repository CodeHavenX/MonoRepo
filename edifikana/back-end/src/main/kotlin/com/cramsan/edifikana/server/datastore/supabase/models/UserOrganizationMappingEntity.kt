package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.OrgMemberStatus
import com.cramsan.edifikana.lib.model.OrgRole
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Entity representing a mapping between a user and an organization.
 */
@Serializable
@SupabaseModel
data class UserOrganizationMappingEntity(
    val id: String,
    @SerialName("user_id")
    val userId: UserId,
    @SerialName("organization_id")
    val organizationId: OrganizationId,
    val role: OrgRole,
    val status: OrgMemberStatus,
    @SerialName("invited_by")
    val invitedBy: String?,
    @SerialName("joined_at")
    val joinedAt: Instant?,
) {

    /**
     * Entity used to insert a new user–organization mapping row for directly-added members.
     *
     * Only [userId], [organizationId], and [role] are sent — all other columns
     * ([status], [invitedBy], [joinedAt]) are intentionally omitted so that DB
     * defaults apply: status defaults to ACTIVE, invitedBy and joinedAt are NULL.
     * The invite-acceptance flow uses a separate update path to set those columns.
     */
    @Serializable
    @SupabaseModel
    data class CreateUserOrganizationMappingEntity(
        @SerialName("user_id")
        val userId: UserId,
        @SerialName("organization_id")
        val organizationId: OrganizationId ,
        val role: OrgRole,
    )

    /**
     * Entity used to upsert a membership row when a user accepts an invite.
     *
     * Explicitly sets [status] to ACTIVE and [joinedAt] to the acceptance time.
     * On conflict (user already has a row for this org), all fields are updated
     * so a previously-inactive member is reactivated with the new role.
     */
    @Serializable
    @SupabaseModel
    data class AcceptInviteEntity(
        @SerialName("user_id")
        val userId: String,
        @SerialName("organization_id")
        val organizationId: String,
        val role: OrgRole,
        val status: OrgMemberStatus,
        @SerialName("joined_at")
        val joinedAt: Instant,
    )

    companion object {
        const val COLLECTION = "user_organization_mapping"
    }
}
