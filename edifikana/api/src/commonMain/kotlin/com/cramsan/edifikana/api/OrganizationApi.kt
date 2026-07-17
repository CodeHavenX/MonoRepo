package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.organization.CreateOrganizationNetworkRequest
import com.cramsan.edifikana.lib.model.network.organization.OrganizationNetworkListNetworkResponse
import com.cramsan.edifikana.lib.model.network.organization.OrganizationNetworkResponse
import com.cramsan.edifikana.lib.model.network.organization.UpdateOrganizationNetworkRequest
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for organization related operations.
 */

object OrganizationApi : Api("organization") {
    val getOrganizationList = operation<
        NoRequestBody,
        NoQueryParam,
        NoPathParam,
        OrganizationNetworkListNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "List organizations",
        description = "Lists all organizations the authenticated user is a member of.",
        responses = UniversalResponsesOnly,
    )

    val getOrganization = operation<
        NoRequestBody,
        NoQueryParam,
        OrganizationId,
        OrganizationNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "Get an organization",
        description = "Retrieves a single organization by its identifier.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No organization exists for the given id."
        },
    )

    val createOrganization = operation<
        CreateOrganizationNetworkRequest,
        NoQueryParam,
        NoPathParam,
        OrganizationNetworkResponse,
    >(
        method = HttpMethod.Post,
        summary = "Create an organization",
        description = "Creates a new organization with the authenticated user as its owner.",
        responses = UniversalResponsesOnly,
    )

    val updateOrganization = operation<
        UpdateOrganizationNetworkRequest,
        NoQueryParam,
        OrganizationId,
        OrganizationNetworkResponse,
    >(
        method = HttpMethod.Put,
        summary = "Update an organization",
        description = "Updates the mutable fields of an existing organization. Requires the ADMIN role or higher.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No organization exists for the given id."
        },
    )
}
