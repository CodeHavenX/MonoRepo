package com.cramsan.edifikana.server.core.datastore.supabase.models

import com.cramsan.framework.ammotations.SupabaseModel
import kotlinx.serialization.Serializable

/**
 * Entity representing a property.
 */
@Serializable
@SupabaseModel
data class PropertyEntity(
    val id: String,
    val name: String,
) {
    companion object {
        const val COLLECTION = "properties"
    }

    /**
     * Entity representing a new property.
     */
    @Serializable
    @SupabaseModel
    data class CreatePropertyEntity(
        val name: String,
    )
}
