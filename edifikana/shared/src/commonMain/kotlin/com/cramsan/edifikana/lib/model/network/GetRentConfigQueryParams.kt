package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for retrieving a rent configuration.
 */
@NetworkModel
@Serializable
data class GetRentConfigQueryParams(
    @SerialName("org_id") val orgId: OrganizationId,
) : QueryParam
