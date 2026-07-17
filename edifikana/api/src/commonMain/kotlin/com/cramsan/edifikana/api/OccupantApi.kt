package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.occupant.CreateOccupantNetworkRequest
import com.cramsan.edifikana.lib.model.network.occupant.GetOccupantsForUnitQueryParams
import com.cramsan.edifikana.lib.model.network.occupant.OccupantListNetworkResponse
import com.cramsan.edifikana.lib.model.network.occupant.OccupantNetworkResponse
import com.cramsan.edifikana.lib.model.network.occupant.UpdateOccupantNetworkRequest
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for occupant operations.
 *
 * Occupants are people associated with a unit — tenants or residents.
 * Write operations require ADMIN role or higher. Read operations require EMPLOYEE role or higher.
 */

object OccupantApi : Api("occupants") {
    val createOccupant =
        operation<
            CreateOccupantNetworkRequest,
            NoQueryParam,
            NoPathParam,
            OccupantNetworkResponse,
            >(
            method = HttpMethod.Post,
            summary = "Create an occupant",
            description = "Creates a new occupant record for a unit. Requires the ADMIN role or higher.",
            responses = UniversalResponsesOnly,
        )

    val getOccupant =
        operation<
            NoRequestBody,
            NoQueryParam,
            OccupantId,
            OccupantNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "Get an occupant",
            description = "Retrieves a single occupant record by its identifier. Requires the EMPLOYEE role or higher.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No occupant exists for the given id."
            },
        )

    val listOccupantsForUnit =
        operation<
            NoRequestBody,
            GetOccupantsForUnitQueryParams,
            NoPathParam,
            OccupantListNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "List occupants for a unit",
            description =
            "Lists occupant records for a unit, optionally including inactive records. " +
                "Requires the EMPLOYEE role or higher.",
            responses = UniversalResponsesOnly,
        )

    val updateOccupant =
        operation<
            UpdateOccupantNetworkRequest,
            NoQueryParam,
            OccupantId,
            OccupantNetworkResponse,
            >(
            method = HttpMethod.Put,
            summary = "Update an occupant",
            description =
            "Updates the mutable fields of an existing occupant record. " +
                "Requires the ADMIN role or higher.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No occupant exists for the given id."
            },
        )

    val removeOccupant =
        operation<
            NoRequestBody,
            NoQueryParam,
            OccupantId,
            OccupantNetworkResponse,
            >(
            method = HttpMethod.Delete,
            summary = "Remove an occupant",
            description = "Removes an occupant record by its identifier. Requires the ADMIN role or higher.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No occupant exists for the given id."
                HttpStatusCode.Conflict describedAs
                    "The occupant is primary and other active occupants still exist for the unit."
            },
        )
}
