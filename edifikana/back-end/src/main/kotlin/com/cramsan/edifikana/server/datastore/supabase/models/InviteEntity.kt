package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.InviteRole
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Supabase entity representing an invite to join an organization.
 */
@Serializable
@SupabaseModel
data class InviteEntity(
    @SerialName("id")
    val id: String,
    @SerialName("email")
    val email: String,
    @SerialName("organization_id")
    val organizationId: OrganizationId,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("expiration")
    val expiration: Instant,
    @SerialName("role")
    val role: InviteRole,
    @SerialName("invite_code")
    val inviteCode: String,
    @SerialName("invited_by")
    val invitedBy: UserId? = null,
    @SerialName("accepted_at")
    val acceptedAt: Instant? = null,
    @SerialName("unit_id")
    val unitId: UnitId? = null,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null,
) {

    /**
     * Supabase entity representing a create invite request.
     */
    @Serializable
    @SupabaseModel
    data class Create(
        @SerialName("email")
        val email: String,
        @SerialName("organization_id")
        val organizationId: OrganizationId,
        @SerialName("created_at")
        val createdAt: Instant,
        @SerialName("expiration")
        val expiration: Instant,
        @SerialName("role")
        val role: InviteRole,
        @SerialName("invite_code")
        val inviteCode: String,
        @SerialName("invited_by")
        val invitedBy: UserId? = null,
        @SerialName("unit_id")
        val unitId: UnitId? = null,
    )

    companion object {
        const val COLLECTION = "invites"
    }
}
