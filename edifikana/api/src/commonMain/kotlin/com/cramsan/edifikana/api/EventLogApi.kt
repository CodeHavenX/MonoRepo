package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.eventLog.EventLogEntryId
import com.cramsan.edifikana.lib.model.network.eventLog.CreateEventLogEntryNetworkRequest
import com.cramsan.edifikana.lib.model.network.eventLog.EventLogEntryListNetworkResponse
import com.cramsan.edifikana.lib.model.network.eventLog.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.eventLog.GetEventLogEntriesQueryParams
import com.cramsan.edifikana.lib.model.network.eventLog.UpdateEventLogEntryNetworkRequest
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for event log related operations.
 */

object EventLogApi : Api("event_log") {
    val createEventLogEntry = operation<
        CreateEventLogEntryNetworkRequest,
        NoQueryParam,
        NoPathParam,
        EventLogEntryNetworkResponse,
    >(
        method = HttpMethod.Post,
        summary = "Create an event log entry",
        description = "Creates a new event log entry for a property.",
        responses = UniversalResponsesOnly,
    )

    val getEventLogEntry = operation<
        NoRequestBody,
        NoQueryParam,
        EventLogEntryId,
        EventLogEntryNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "Get an event log entry",
        description = "Retrieves a single event log entry by its identifier.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No event log entry exists for the given id."
        },
    )

    val getEventLogEntries = operation<
        NoRequestBody,
        GetEventLogEntriesQueryParams,
        NoPathParam,
        EventLogEntryListNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "List event log entries",
        description = "Lists event log entries for a property.",
        responses = UniversalResponsesOnly,
    )

    val updateEventLogEntry = operation<
        UpdateEventLogEntryNetworkRequest,
        NoQueryParam,
        EventLogEntryId,
        EventLogEntryNetworkResponse,
    >(
        method = HttpMethod.Put,
        summary = "Update an event log entry",
        description = "Updates the mutable fields of an existing event log entry.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No event log entry exists for the given id."
        },
    )

    val deleteEventLogEntry = operation<
        NoRequestBody,
        NoQueryParam,
        EventLogEntryId,
        NoResponseBody,
    >(
        method = HttpMethod.Delete,
        summary = "Delete an event log entry",
        description = "Permanently deletes an event log entry by its identifier.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No event log entry exists for the given id."
        },
    )
}
