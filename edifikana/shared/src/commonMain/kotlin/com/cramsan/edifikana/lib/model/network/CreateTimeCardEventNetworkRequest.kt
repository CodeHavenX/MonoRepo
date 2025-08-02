package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.framework.ammotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for creating a time card event.
 */
@NetworkModel
@Serializable
data class CreateTimeCardEventNetworkRequest(
    @SerialName("staff_id")
    val staffId: String,
    @SerialName("fallback_staff_name")
    val fallbackStaffName: String?,
    @SerialName("property_id")
    val propertyId: String,
    val type: TimeCardEventType,
    @SerialName("image_url")
    val imageUrl: String?,
)
