package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.rent.RentConfigNetworkRequest
import com.cramsan.edifikana.lib.model.network.rent.RentConfigNetworkResponse
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for rent configuration operations.
 *
 * Rent config is unit-scoped and uses upsert semantics (PUT creates or updates).
 * Write operations require ADMIN role or higher. Read operations require EMPLOYEE role or higher.
 */

object RentConfigApi : Api("rent-config") {
    val getRentConfig =
        operation<
            NoRequestBody,
            NoQueryParam,
            UnitId,
            RentConfigNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "Get rent configuration",
            description = "Retrieves the rent configuration for a unit. Requires the EMPLOYEE role or higher.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No rent configuration exists for the given unit."
            },
        )

    val setRentConfig =
        operation<
            RentConfigNetworkRequest,
            NoQueryParam,
            UnitId,
            RentConfigNetworkResponse,
            >(
            method = HttpMethod.Put,
            summary = "Set rent configuration",
            description =
            "Creates or updates (upserts) the rent configuration for a unit. " +
                "Requires the ADMIN role or higher.",
            responses = UniversalResponsesOnly,
        )
}
