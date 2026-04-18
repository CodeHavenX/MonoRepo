package com.cramsan.edifikana.lib.model.network.unit

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update an existing unit.
 *
 * All fields are optional. A null value means "leave unchanged" — the existing database value
 * is preserved. It is not possible to explicitly clear a nullable field (e.g. reset [notes] to
 * null) through this request.
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
