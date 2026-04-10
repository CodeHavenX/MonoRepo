package com.cramsan.edifikana.server.datastore.supabase.models

import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.user.UserId
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
    val userId: UserId,
    @SerialName("property_id")
    val propertyId: PropertyId,
) {

    /**
     * Creates a new instance of [UserPropertyMappingEntity].
     */
    @Serializable
    @SupabaseModel
    data class CreateUserPropertyMappingEntity(
        @SerialName("user_id")
        val userId: UserId,
        @SerialName("property_id")
        val propertyId: PropertyId,
    )

    companion object {
        const val COLLECTION = "user_property_mapping"
    }
}
