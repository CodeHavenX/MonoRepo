package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.service.models.Property
import com.cramsan.framework.annotations.SupabaseModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Entity for the v_user_properties view.
 * Contains property data plus the user_id from the mapping.
 */
@Serializable
@SupabaseModel
data class UserPropertyViewEntity(
    val id: String,
    val name: String,
    val address: String,
    @SerialName("organization_id")
    val organizationId: String,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("user_id")
    val userId: String,
) {
    /**
     * Converts this view entity to a Property domain model.
     */
    fun toProperty(): Property = Property(
        id = PropertyId(id),
        name = name,
        address = address,
        organizationId = OrganizationId(organizationId),
        imageUrl = imageUrl,
    )
}
