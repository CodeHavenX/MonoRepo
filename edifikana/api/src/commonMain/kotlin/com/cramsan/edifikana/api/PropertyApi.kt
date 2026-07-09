package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.property.CreatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.network.property.PropertyListNetworkResponse
import com.cramsan.edifikana.lib.model.network.property.PropertyNetworkResponse
import com.cramsan.edifikana.lib.model.network.property.UpdatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.AllowedResponses
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for property related operations.
 */

object PropertyApi : Api("property") {
    val createProperty =
        operation<
            CreatePropertyNetworkRequest,
            NoQueryParam,
            NoPathParam,
            PropertyNetworkResponse,
            >(
            method = HttpMethod.Post,
            summary = "Create a property",
            description = "Creates a new property within the caller's organization. Requires the ADMIN role.",
            responses = AllowedResponses { },
        )

    val getProperty =
        operation<
            NoRequestBody,
            NoQueryParam,
            PropertyId,
            PropertyNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "Get a property",
            description = "Retrieves a single property by its identifier. Requires the MANAGER role or higher.",
            responses =
            AllowedResponses {
                HttpStatusCode.NotFound describedAs "No property exists for the given id."
            },
        )

    val getAssignedProperties =
        operation<
            NoRequestBody,
            NoQueryParam,
            NoPathParam,
            PropertyListNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "List assigned properties",
            description = "Returns all properties the authenticated user has been assigned access to.",
            responses = AllowedResponses { },
        )

    val updateProperty =
        operation<
            UpdatePropertyNetworkRequest,
            NoQueryParam,
            PropertyId,
            PropertyNetworkResponse,
            >(
            method = HttpMethod.Put,
            summary = "Update a property",
            description = "Updates the mutable fields of an existing property. Requires the ADMIN role.",
            responses = AllowedResponses { },
        )

    val deleteProperty =
        operation<
            NoRequestBody,
            NoQueryParam,
            PropertyId,
            NoResponseBody,
            >(
            method = HttpMethod.Delete,
            summary = "Delete a property",
            description = "Permanently deletes a property by its identifier. Requires the ADMIN role.",
            responses = AllowedResponses { },
        )
}
