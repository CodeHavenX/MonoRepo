package com.cramsan.edifikana.server.core.datastore.supabase.models

import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity representing a property.
 */
@Serializable
@SupabaseModel
data class UserPropertyMappingEntity(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("property_id")
    val propertyId: String,
) {

    /**
     * Creates a new instance of [UserPropertyMappingEntity].
     */
    @Serializable
    @SupabaseModel
    data class CreateUserPropertyMappingEntity(
        @SerialName("user_id")
        val userId: String,
        @SerialName("property_id")
        val propertyId: String,
    )

    companion object {
        const val COLLECTION = "user_property_mapping"
    }
}
