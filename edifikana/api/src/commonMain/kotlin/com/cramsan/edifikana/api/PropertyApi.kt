package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.property.CreatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.network.property.PropertyListNetworkResponse
import com.cramsan.edifikana.lib.model.network.property.PropertyNetworkResponse
import com.cramsan.edifikana.lib.model.network.property.UpdatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.organization.OrganizationId
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
            responses = UniversalResponsesOnly,
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
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No property exists for the given id."
            },
        )

    val getProperties =
        operation<
            NoRequestBody,
            NoQueryParam,
            OrganizationId,
            PropertyListNetworkResponse,
            >(
            method = HttpMethod.Get,
            path = "by-organization",
            summary = "List properties for an organization",
            description = "Returns all properties in the given organization. Requires membership in the organization.",
            responses = UniversalResponsesOnly,
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
            responses = UniversalResponsesOnly,
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
            responses = UniversalResponsesOnly,
        )
}
