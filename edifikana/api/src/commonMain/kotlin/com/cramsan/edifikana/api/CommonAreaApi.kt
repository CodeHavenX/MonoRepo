package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.network.commonArea.CommonAreaListNetworkResponse
import com.cramsan.edifikana.lib.model.network.commonArea.CommonAreaNetworkResponse
import com.cramsan.edifikana.lib.model.network.commonArea.CreateCommonAreaNetworkRequest
import com.cramsan.edifikana.lib.model.network.commonArea.UpdateCommonAreaNetworkRequest
import com.cramsan.edifikana.lib.model.property.PropertyId
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
 * API definition for common area operations within a property.
 *
 * Common areas are shared spaces (e.g. Lobby, Pool, Gym) that belong to a single property.
 * All operations require MANAGER role or higher in the property's organization, depending on
 * the operation. In some cases it will be ADMIN+, in others it will be MANAGER+
 */

object CommonAreaApi : Api("common-area") {
    val createCommonArea = operation<
        CreateCommonAreaNetworkRequest,
        NoQueryParam,
        NoPathParam,
        CommonAreaNetworkResponse,
    >(
        method = HttpMethod.Post,
        summary = "Create a common area",
        description = "Creates a new common area within a property. Requires the MANAGER role or higher.",
        responses = UniversalResponsesOnly,
    )

    val getCommonArea = operation<
        NoRequestBody,
        NoQueryParam,
        CommonAreaId,
        CommonAreaNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "Get a common area",
        description = "Retrieves a single common area by its identifier. Requires the MANAGER role or higher.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No common area exists for the given id."
        },
    )

    val getCommonAreasForProperty = operation<
        NoRequestBody,
        NoQueryParam,
        PropertyId,
        CommonAreaListNetworkResponse,
    >(
        method = HttpMethod.Get,
        path = "by-property",
        summary = "List common areas for a property",
        description = "Lists all common areas belonging to a property. Requires the MANAGER role or higher.",
        responses = UniversalResponsesOnly,
    )

    val updateCommonArea = operation<
        UpdateCommonAreaNetworkRequest,
        NoQueryParam,
        CommonAreaId,
        CommonAreaNetworkResponse,
    >(
        method = HttpMethod.Put,
        summary = "Update a common area",
        description = "Updates the mutable fields of an existing common area. Requires the MANAGER role or higher.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No common area exists for the given id."
        },
    )

    val deleteCommonArea = operation<
        NoRequestBody,
        NoQueryParam,
        CommonAreaId,
        NoResponseBody,
    >(
        method = HttpMethod.Delete,
        summary = "Delete a common area",
        description = "Permanently deletes a common area by its identifier. Requires the MANAGER role or higher.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No common area exists for the given id."
        },
    )
}
