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
    val unitNumber: String? = null,
    @SerialName("bedrooms")
    val bedrooms: Int? = null,
    @SerialName("bathrooms")
    val bathrooms: Int? = null,
    @SerialName("sq_ft")
    val sqFt: Int? = null,
    @SerialName("floor")
    val floor: Int? = null,
    @SerialName("notes")
    val notes: String? = null,
) : RequestBody
