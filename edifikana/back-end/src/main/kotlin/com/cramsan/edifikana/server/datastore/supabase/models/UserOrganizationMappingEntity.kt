package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.OrgMemberStatus
import com.cramsan.edifikana.lib.model.OrgRole
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.EncodeDefault
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
    val userId: String,
    @SerialName("organization_id")
    val organizationId: String,
    val role: OrgRole,
    val status: OrgMemberStatus,
    @SerialName("invited_by")
    val invitedBy: String?,
    @SerialName("joined_at")
    val joinedAt: Instant?,
) {

    /**
     * Entity used to insert a new user–organization mapping row.
     *
     * [status] and [invitedBy] are nullable to allow DB defaults to apply:
     * status defaults to ACTIVE, invitedBy is NULL for directly-added members.
     * [joinedAt] is intentionally excluded — it is only set during the invite
     * acceptance flow and is handled separately.
     */
    @Serializable
    @SupabaseModel
    data class CreateUserOrganizationMappingEntity(
        @SerialName("user_id")
        val userId: String,
        @SerialName("organization_id")
        val organizationId: String,
        val role: OrgRole?,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        val status: OrgMemberStatus? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER)
        @SerialName("invited_by")
        val invitedBy: String? = null,
    )

    companion object {
        const val COLLECTION = "user_organization_mapping"
    }
}
