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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementation of [OrganizationService] that interacts with the backend to fetch organization data.
 */
class OrganizationServiceImpl(
    private val http: HttpClient,
) : OrganizationService {

    private val _activeOrganization: MutableStateFlow<Organization?> = MutableStateFlow(null)

    override val observableActiveOrganization: StateFlow<Organization?>
        get() = _activeOrganization.asStateFlow()

    override suspend fun setActiveOrganization(organizationId: OrganizationId): Result<Unit> = runSuspendCatching(TAG) {
        val organizationResult = getOrganization(organizationId).getOrThrow()
        _activeOrganization.value = organizationResult
    }

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
