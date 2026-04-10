package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a unit.
 */
@NetworkModel
@Serializable
data class UnitNetworkResponse(
    @SerialName("unit_id")
    val unitId: UnitId,
    @SerialName("property_id")
    val propertyId: PropertyId,
    @SerialName("org_id")
    val orgId: OrganizationId,
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
) : ResponseBody
