package com.cramsan.edifikana.server.core.datastore.supabase.models

import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity representing a property.
 */
@Serializable
@SupabaseModel
data class PropertyEntity(
    val id: String,
    val name: String,
    val address: String,
    @SerialName("organization_id")
    val organizationId: String,
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
        val address: String,
        @SerialName("organization_id")
        val organizationId: String,
    )
}
