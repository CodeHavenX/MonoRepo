package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model representing a staff.
 */
@NetworkModel
@Serializable
data class StaffNetworkResponse(
    val id: String,
    @SerialName("id_type")
    val idType: IdType,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val role: StaffRole,
    @SerialName("property_id")
    val propertyId: String,
)
