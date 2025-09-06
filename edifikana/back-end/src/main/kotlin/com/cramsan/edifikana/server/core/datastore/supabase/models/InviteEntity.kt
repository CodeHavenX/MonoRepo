package com.cramsan.edifikana.server.core.datastore.supabase.models

import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase entity representing an invite to join an organization.
 */
@Serializable
@SupabaseModel
data class InviteEntity(
    @SerialName("email")
    val email: String,
    @SerialName("organization_id")
    val organizationId: String,
) {

    companion object {
        const val COLLECTION = "invites"
    }
}
