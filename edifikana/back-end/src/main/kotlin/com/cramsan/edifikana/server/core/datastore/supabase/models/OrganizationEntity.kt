package com.cramsan.edifikana.server.core.datastore.supabase.models

import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.Serializable

/**
 * Supabase entity representing an organization.
 */
@Serializable
@SupabaseModel
data class OrganizationEntity(
    val id: String,
) {
    companion object {
        const val COLLECTION = "organizations"
    }

    /**
     * Supabase entity representing a create organization request.
     */
    @Serializable
    @SupabaseModel
    data object CreateOrganizationEntity
}
