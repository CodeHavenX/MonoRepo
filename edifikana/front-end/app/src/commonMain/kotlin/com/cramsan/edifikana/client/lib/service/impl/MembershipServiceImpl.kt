package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.api.MembershipApi
import com.cramsan.edifikana.client.lib.models.OrgMemberModel
import com.cramsan.edifikana.client.lib.service.MembershipService
import com.cramsan.edifikana.lib.model.network.organization.MemberNetworkResponse
import com.cramsan.edifikana.lib.model.network.organization.TransferOwnershipNetworkRequest
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import io.ktor.client.HttpClient

/**
 * Implementation of [MembershipService] that calls the backend membership API.
 */
@FrontendService
class MembershipServiceImpl(private val http: HttpClient) : MembershipService {
    override suspend fun listMembers(orgId: OrganizationId): Result<List<OrgMemberModel>> =
        runSuspendCatching(TAG) {
            val response =
                MembershipApi
                    .listMembers
                    .buildRequest(orgId)
                    .execute(http)
            response.content.map { it.toOrgMemberModel() }
        }

    override suspend fun leaveOrganization(orgId: OrganizationId): Result<Unit> =
        runSuspendCatching(TAG) {
            MembershipApi
                .leaveOrganization
                .buildRequest(orgId)
                .execute(http)
        }

    override suspend fun transferOwnership(orgId: OrganizationId, newOwnerId: UserId): Result<Unit> =
        runSuspendCatching(TAG) {
            MembershipApi
                .transferOwnership
                .buildRequest(orgId, TransferOwnershipNetworkRequest(newOwnerId))
                .execute(http)
        }

    companion object {
        private const val TAG = "MembershipServiceImpl"
    }
}

private fun MemberNetworkResponse.toOrgMemberModel() =
    OrgMemberModel(
        userId = userId,
        orgId = orgId,
        role = role,
        status = status,
        joinedAt = joinedAt,
        displayName = displayName,
        email = email.email,
    )
