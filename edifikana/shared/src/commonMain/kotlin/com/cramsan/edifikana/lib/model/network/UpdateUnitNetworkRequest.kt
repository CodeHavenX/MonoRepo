package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update an existing unit.
 */
@NetworkModel
@Serializable
data class UpdateUnitNetworkRequest(
    @SerialName("unit_number")
    val unitNumber: String?,
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
