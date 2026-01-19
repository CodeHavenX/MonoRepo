package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase entity representing an organization.
 */
@Serializable
@SupabaseModel
data class OrganizationEntity(
    val id: String,
    val name: String,
    val description: String,
) {
    companion object {
        const val COLLECTION = "organizations"
    }

    /**
     * Supabase entity representing a create organization request.
     */
    @Serializable
    @SupabaseModel
    data class CreateOrganizationEntity(
        @SerialName("name")
        val name: String,
        @SerialName("description")
        val description: String,
    )

    /**
     * Supabase entity representing an update organization request.
     */
    @Serializable
    @SupabaseModel
    data class UpdateOrganizationEntity(
        @SerialName("name")
        val name: String?,
        @SerialName("description")
        val description: String?,
    )
}
