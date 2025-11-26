package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.network.CreateEventLogEntryNetworkRequest
import com.cramsan.edifikana.lib.model.network.EventLogEntryListNetworkResponse
import com.cramsan.edifikana.lib.model.network.EventLogEntryNetworkResponse
import com.cramsan.edifikana.lib.model.network.GetEventLogEntriesQueryParams
import com.cramsan.edifikana.lib.model.network.UpdateEventLogEntryNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for event log related operations.
 */
@OptIn(NetworkModel::class)
object EventLogApi : Api("event_log") {
    val createEventLogEntry = operation<
        CreateEventLogEntryNetworkRequest,
        NoQueryParam,
        NoPathParam,
        EventLogEntryNetworkResponse
        >(HttpMethod.Post)

    val getEventLogEntry = operation<
        NoRequestBody,
        NoQueryParam,
        EventLogEntryId,
        EventLogEntryNetworkResponse
        >(HttpMethod.Get)

    val getEventLogEntries = operation<
        NoRequestBody,
        GetEventLogEntriesQueryParams,
        NoPathParam,
        EventLogEntryListNetworkResponse
        >(HttpMethod.Get)

    val updateEventLogEntry = operation<
        UpdateEventLogEntryNetworkRequest,
        NoQueryParam,
        EventLogEntryId,
        EventLogEntryNetworkResponse
        >(HttpMethod.Put)

    val deleteEventLogEntry = operation<
        NoRequestBody,
        NoQueryParam,
        EventLogEntryId,
        NoResponseBody
        >(HttpMethod.Delete)
}
