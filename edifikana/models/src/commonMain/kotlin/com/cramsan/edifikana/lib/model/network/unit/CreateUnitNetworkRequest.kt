package com.cramsan.edifikana.lib.model.network.unit

import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new unit.
 */
@NetworkModel
@Serializable
data class CreateUnitNetworkRequest(
    @SerialName("property_id")
    val propertyId: PropertyId,
    @SerialName("unit_number")
    val unitNumber: String,
    @SerialName("bedrooms")
    val bedrooms: Int?,
    @SerialName("bathrooms")
    val bathrooms: Int?,
    @SerialName("sq_ft")
    val sqFt: Int?,
    @SerialName("floor")
    val floor: Int?,
    @SerialName("notes")
    val notes: String?,
) : RequestBody
