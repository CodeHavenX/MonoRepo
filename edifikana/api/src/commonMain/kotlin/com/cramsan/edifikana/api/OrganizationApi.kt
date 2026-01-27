package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.network.CreateOrganizationNetworkRequest
import com.cramsan.edifikana.lib.model.network.OrganizationNetworkListNetworkResponse
import com.cramsan.edifikana.lib.model.network.OrganizationNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdateOrganizationNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for organization related operations.
 */
@OptIn(NetworkModel::class)
object OrganizationApi : Api("organization") {
    val getOrganizationList = operation<
        NoRequestBody,
        NoQueryParam,
        NoPathParam,
        OrganizationNetworkListNetworkResponse,
        >(HttpMethod.Get)

    val getOrganization = operation<
        NoRequestBody,
        NoQueryParam,
        OrganizationId,
        OrganizationNetworkResponse,
        >(HttpMethod.Get)

    val createOrganization = operation<
        CreateOrganizationNetworkRequest,
        NoQueryParam,
        NoPathParam,
        OrganizationNetworkResponse,
        >(HttpMethod.Post)

    val updateOrganization = operation<
        UpdateOrganizationNetworkRequest,
        NoQueryParam,
        OrganizationId,
        OrganizationNetworkResponse,
        >(HttpMethod.Put)
}
