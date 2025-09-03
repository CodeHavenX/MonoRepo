package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.Organization
import com.cramsan.edifikana.client.lib.service.OrganizationService
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.network.OrganizationNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.runSuspendCatching
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * Implementation of [OrganizationService] that interacts with the backend to fetch organization data.
 */
class OrganizationServiceImpl(
    private val http: HttpClient,
) : OrganizationService {

    @OptIn(NetworkModel::class)
    override suspend fun getOrganization(
        organizationId: OrganizationId,
    ): Result<Organization> = runSuspendCatching(TAG) {
        val response = http.get("${Routes.Organization.PATH}/$organizationId").body<OrganizationNetworkResponse>()
        response.toOrganizationModel()
    }

    @OptIn(NetworkModel::class)
    override suspend fun getOrganizations(): Result<List<Organization>> = runSuspendCatching(TAG) {
        val response = http.get(Routes.Organization.PATH).body<List<OrganizationNetworkResponse>>()
        response.map { it.toOrganizationModel() }
    }

    companion object {
        private const val TAG = "OrganizationServiceImpl"
    }
}
