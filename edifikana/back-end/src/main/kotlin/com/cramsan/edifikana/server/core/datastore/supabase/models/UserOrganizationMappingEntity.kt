package com.cramsan.edifikana.server.core.datastore.supabase.models

import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
) {

    /**
     * Creates a new instance of [UserOrganizationMappingEntity].
     */
    @Serializable
    @SupabaseModel
    data class CreateUserOrganizationMappingEntity(
        @SerialName("user_id")
        val userId: String,
        @SerialName("organization_id")
        val organizationId: String,
    )

    companion object {
        const val COLLECTION = "user_organization_mapping"
    }
}
