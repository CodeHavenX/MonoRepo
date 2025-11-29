package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.Serializable

/**
 * Query parameters for getting event log entries.
 */
@NetworkModel
@Serializable
data class GetEventLogEntriesQueryParams(
    val propertyId: PropertyId,
) : QueryParam
