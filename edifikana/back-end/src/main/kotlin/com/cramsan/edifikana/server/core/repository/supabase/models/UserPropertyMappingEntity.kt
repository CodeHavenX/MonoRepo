package com.cramsan.edifikana.server.core.repository.supabase.models

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.core.repository.supabase.SupabaseModel
import kotlinx.serialization.Serializable

/**
 * Entity representing a property.
 */
@Serializable
@SupabaseModel
data class UserPropertyMappingEntity(
    val id: String,
    val userId: String,
    val propertyId: String,
) {
    companion object {
        const val COLLECTION = "user_property_mapping"
    }
}
