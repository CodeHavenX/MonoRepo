package com.cramsan.edifikana.lib.model.network.eventLog

import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Query parameters for getting event log entries.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters for listing event log entries, requiring a property id.")
data class GetEventLogEntriesQueryParams(
    @JsonSchema.Description("Identifier of the property to list event log entries for.")
    val propertyId: PropertyId,
) : QueryParam
