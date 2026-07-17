package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.unit.CreateUnitNetworkRequest
import com.cramsan.edifikana.lib.model.network.unit.GetUnitsQueryParams
import com.cramsan.edifikana.lib.model.network.unit.UnitListNetworkResponse
import com.cramsan.edifikana.lib.model.network.unit.UnitNetworkResponse
import com.cramsan.edifikana.lib.model.network.unit.UpdateUnitNetworkRequest
import com.cramsan.edifikana.lib.model.unit.UnitId
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
 * API definition for unit CRUD operations.
 */

object UnitApi : Api("unit") {
    val createUnit = operation<
        CreateUnitNetworkRequest,
        NoQueryParam,
        NoPathParam,
        UnitNetworkResponse,
    >(
        method = HttpMethod.Post,
        summary = "Create a unit",
        description = "Creates a new unit within a property. Requires the ADMIN role.",
        responses = UniversalResponsesOnly,
    )

    val getUnit = operation<
        NoRequestBody,
        NoQueryParam,
        UnitId,
        UnitNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "Get a unit",
        description = "Retrieves a single unit by its identifier. Requires the MANAGER role or higher.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No unit exists for the given id."
        },
    )

    val getUnits = operation<
        NoRequestBody,
        GetUnitsQueryParams,
        NoPathParam,
        UnitListNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "List units",
        description = "Lists all units belonging to a property. Requires the MANAGER role or higher.",
        responses = UniversalResponsesOnly,
    )

    val updateUnit = operation<
        UpdateUnitNetworkRequest,
        NoQueryParam,
        UnitId,
        UnitNetworkResponse,
    >(
        method = HttpMethod.Put,
        summary = "Update a unit",
        description = "Updates the mutable fields of an existing unit. Requires the ADMIN role.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No unit exists for the given id."
        },
    )

    val deleteUnit = operation<
        NoRequestBody,
        NoQueryParam,
        UnitId,
        NoResponseBody,
    >(
        method = HttpMethod.Delete,
        summary = "Delete a unit",
        description = "Permanently deletes a unit by its identifier. Requires the ADMIN role.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No unit exists for the given id."
        },
    )
}
