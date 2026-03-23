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
 * Supabase entity representing a row from the v_org_members view.
 * Combines user_organization_mapping columns with user profile data (email, first_name, last_name).
 * joined_at is returned as epoch seconds (BIGINT) from the view.
 */
@Serializable
@SupabaseModel
data class OrgMemberViewEntity(
    @SerialName("user_id")
    val userId: UserId,
    @SerialName("organization_id")
    val organizationId: OrganizationId,
    val role: OrgRole,
    val status: OrgMemberStatus,
    @SerialName("joined_at")
    val joinedAt: Instant?,
    val email: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
) {
    companion object {
        const val COLLECTION = "v_org_members"
    }
}
