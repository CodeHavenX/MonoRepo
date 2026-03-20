package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for listing documents.
 */
@NetworkModel
@Serializable
data class GetDocumentsQueryParams(
    @SerialName("org_id") val orgId: OrganizationId,
    @SerialName("property_id") val propertyId: PropertyId? = null,
    @SerialName("unit_id") val unitId: UnitId? = null,
) : QueryParam
