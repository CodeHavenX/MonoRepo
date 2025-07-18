package com.cramsan.edifikana.server.core.datastore.supabase.models

import com.cramsan.edifikana.server.core.datastore.supabase.SupabaseModel
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
    companion object {
        const val COLLECTION = "user_property_mapping"
    }
}
