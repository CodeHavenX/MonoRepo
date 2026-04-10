package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing units.
 */
@NetworkModel
@Serializable
data class GetUnitsQueryParams(
    @SerialName("property_id") val propertyId: PropertyId,
) : QueryParam
