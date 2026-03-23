package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.MembershipApi
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.InviteRole
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.network.InviteListNetworkResponse
import com.cramsan.edifikana.lib.model.network.InviteMemberNetworkRequest
import com.cramsan.edifikana.lib.model.network.JoinViaCodeNetworkRequest
import com.cramsan.edifikana.lib.model.network.MemberListNetworkResponse
import com.cramsan.edifikana.lib.model.network.RemoveMemberNetworkRequest
import com.cramsan.edifikana.lib.model.network.TransferOwnershipNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateRoleNetworkRequest
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.datastore.supabase.toUserRole
import com.cramsan.edifikana.server.service.MembershipService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.utils.exceptions.InvalidRequestException
import com.cramsan.framework.utils.exceptions.UnauthorizedException
import io.ktor.server.routing.Routing

/**
 * Controller for organization membership management operations.
 */
class MembershipController(
    private val membershipService: MembershipService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
    private val rbacService: RBACService,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action."

    /**
     * Invites a user to the organization. Requires MANAGER+.
     * RESIDENT role is rejected at the API boundary.
     * Inviter cannot assign a role higher than their own.
     */
    @OptIn(NetworkModel::class)
    @Suppress("ThrowsCount")
    suspend fun inviteMember(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        orgId: OrganizationId,
        request: InviteMemberNetworkRequest,
    ): NoResponseBody {
        if (request.role == InviteRole.RESIDENT) {
            throw InvalidRequestException(
                "Residents must be invited from a unit. Please invite residents via their unit instead of the organization.")
        }
        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val inviterRole = rbacService.getUserRoleForOrganizationAction(context, orgId)
        if (request.role.toUserRole().level < inviterRole.level) {
            throw UnauthorizedException("Cannot invite users with higher privileges than your own")
        }
        membershipService.inviteMember(orgId, request.email, request.role).requireSuccess()
        return NoResponseBody
    }

    /**
     * Lists all active members of the organization. Requires EMPLOYEE+.
     */
    @OptIn(NetworkModel::class)
    suspend fun listMembers(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        orgId: OrganizationId,
    ): MemberListNetworkResponse {
        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val members = membershipService.listMembers(orgId).getOrThrow()
            .map { it.toMemberNetworkResponse() }
        return MemberListNetworkResponse(members)
    }

    /**
     * Updates a member's role. Requires ADMIN+.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateMemberRole(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        orgId: OrganizationId,
        request: UpdateRoleNetworkRequest,
    ): NoResponseBody {
        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        membershipService.updateMemberRole(orgId, request.userId, request.role).requireSuccess()
        return NoResponseBody
    }

    /**
     * Removes a member from the organization. Requires MANAGER+.
     */
    @OptIn(NetworkModel::class)
    suspend fun removeMember(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        orgId: OrganizationId,
        request: RemoveMemberNetworkRequest,
    ): NoResponseBody {
        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        membershipService.removeMember(orgId, request.userId).requireSuccess()
        return NoResponseBody
    }

    /**
     * Leaves the organization as the authenticated user. Requires active membership (EMPLOYEE+).
     * The sole owner cannot leave.
     */
    @OptIn(NetworkModel::class)
    suspend fun leaveOrganization(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        orgId: OrganizationId,
    ): NoResponseBody {
        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val callerId = context.payload.userId
        membershipService.leaveOrganization(callerId, orgId).requireSuccess()
        return NoResponseBody
    }

    /**
     * Transfers organization ownership to another member. Requires OWNER.
     */
    @OptIn(NetworkModel::class)
    suspend fun transferOwnership(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        orgId: OrganizationId,
        request: TransferOwnershipNetworkRequest,
    ): NoResponseBody {
        if (!rbacService.hasRole(context, orgId, UserRole.OWNER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val callerId = context.payload.userId
        membershipService.transferOwnership(callerId, orgId, request.newOwnerId).requireSuccess()
        return NoResponseBody
    }

    /**
     * Lists pending invites for the organization. Requires MANAGER+.
     */
    @OptIn(NetworkModel::class)
    suspend fun listPendingInvites(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        orgId: OrganizationId,
    ): InviteListNetworkResponse {
        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val invites = membershipService.listPendingInvites(orgId).getOrThrow()
            .map { it.toInviteNetworkResponse() }
        return InviteListNetworkResponse(invites)
    }

    /**
     * Cancels a pending invite. Requires MANAGER+ in the invite's organization.
     */
    @OptIn(NetworkModel::class)
    suspend fun cancelInvite(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        inviteId: InviteId,
    ): NoResponseBody {
        val orgId = membershipService.getInviteOrganization(inviteId).requireSuccess()
        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        membershipService.cancelInvite(inviteId).requireSuccess()
        return NoResponseBody
    }

    /**
     * Resends a pending invite with a new code and reset expiry.
     * Requires MANAGER+ in the invite's organization.
     */
    @OptIn(NetworkModel::class)
    suspend fun resendInvite(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        inviteId: InviteId,
    ): NoResponseBody {
        val orgId = membershipService.getInviteOrganization(inviteId).requireSuccess()
        if (!rbacService.hasRoleOrHigher(context, orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        membershipService.resendInvite(inviteId).requireSuccess()
        return NoResponseBody
    }

    /**
     * Joins an organization via an invite code. Requires authentication only.
     */
    @OptIn(NetworkModel::class)
    suspend fun joinViaCode(
        context: ClientContext.AuthenticatedClientContext<SupabaseContextPayload>,
        request: JoinViaCodeNetworkRequest,
    ): NoResponseBody {
        val callerId = context.payload.userId
        membershipService.joinViaCode(callerId, request.inviteCode).requireSuccess()
        return NoResponseBody
    }

    @OptIn(NetworkModel::class)
    override fun registerRoutes(route: Routing) {
        MembershipApi.register(route) {
            handler(api.inviteMember, contextRetriever) { request ->
                inviteMember(request.context, request.pathParam, request.requestBody)
            }
            handler(api.listMembers, contextRetriever) { request ->
                listMembers(request.context, request.pathParam)
            }
            handler(api.updateMemberRole, contextRetriever) { request ->
                updateMemberRole(request.context, request.pathParam, request.requestBody)
            }
            handler(api.removeMember, contextRetriever) { request ->
                removeMember(request.context, request.pathParam, request.requestBody)
            }
            handler(api.leaveOrganization, contextRetriever) { request ->
                leaveOrganization(request.context, request.pathParam)
            }
            handler(api.transferOwnership, contextRetriever) { request ->
                transferOwnership(request.context, request.pathParam, request.requestBody)
            }
            handler(api.listPendingInvites, contextRetriever) { request ->
                listPendingInvites(request.context, request.pathParam)
            }
            handler(api.cancelInvite, contextRetriever) { request ->
                cancelInvite(request.context, request.pathParam)
            }
            handler(api.resendInvite, contextRetriever) { request ->
                resendInvite(request.context, request.pathParam)
            }
            handler(api.joinViaCode, contextRetriever) { request ->
                joinViaCode(request.context, request.requestBody)
            }
        }
    }
}
