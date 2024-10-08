package com.cramsan.edifikana.lib.model

import com.cramsan.edifikana.lib.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update an existing property.
 */
@NetworkModel
@Serializable
data class UpdateStaffNetworkRequest(
    @SerialName("id_type")
    val idType: IdType?,
    @SerialName("first_name")
    val firstName: String?,
    @SerialName("last_name")
    val lastName: String?,
    val role: StaffRole,
)
