package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Entity representing a property.
 */
@Serializable
@SupabaseModel
data class PropertyEntity(
    val id: PropertyId,
    val name: String,
    val address: String,
    @SerialName("organization_id")
    val organizationId: OrganizationId,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("deleted_at")
    val deletedAt: Instant? = null,
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
        val organizationId: OrganizationId,
        @SerialName("image_url")
        val imageUrl: String? = null,
    )
}
