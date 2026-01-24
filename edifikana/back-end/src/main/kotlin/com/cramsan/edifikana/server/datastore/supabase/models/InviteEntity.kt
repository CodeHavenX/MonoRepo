package com.cramsan.edifikana.server.datastore.supabase.models

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
    val organizationId: String,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("expiration")
    val expiration: Instant,
    @SerialName("role")
    val role: String,
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
        val organizationId: String,
        @SerialName("created_at")
        val createdAt: Instant,
        @SerialName("expiration")
        val expiration: Instant,
        @SerialName("role")
        val role: String,
    )

    companion object {
        const val COLLECTION = "invites"
    }
}
