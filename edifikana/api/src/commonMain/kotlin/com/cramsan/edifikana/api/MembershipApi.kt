package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.network.InviteListNetworkResponse
import com.cramsan.edifikana.lib.model.network.InviteMemberNetworkRequest
import com.cramsan.edifikana.lib.model.network.JoinViaCodeNetworkRequest
import com.cramsan.edifikana.lib.model.network.MemberListNetworkResponse
import com.cramsan.edifikana.lib.model.network.RemoveMemberNetworkRequest
import com.cramsan.edifikana.lib.model.network.TransferOwnershipNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateRoleNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for organization membership management operations.
 *
 * Routes are structured as /membership/{subPath}/{pathParam} to fit the
 * single-path-parameter constraint of the API framework.
 */
@OptIn(NetworkModel::class)
object MembershipApi : Api("membership") {

    /**
     * Invite a new member to the organization.
     * Route: POST /membership/invite/{orgId}
     */
    val inviteMember = operation<
        InviteMemberNetworkRequest,
        NoQueryParam,
        OrganizationId,
        NoResponseBody
        >(HttpMethod.Post, "invite")

    /**
     * List all active members of an organization.
     * Route: GET /membership/members/{orgId}
     */
    val listMembers = operation<
        NoRequestBody,
        NoQueryParam,
        OrganizationId,
        MemberListNetworkResponse
        >(HttpMethod.Get, "members")

    /**
     * Update a member's role within an organization.
     * The target userId and new role are provided in the request body.
     * Route: PUT /membership/members/{orgId}
     */
    val updateMemberRole = operation<
        UpdateRoleNetworkRequest,
        NoQueryParam,
        OrganizationId,
        NoResponseBody
        >(HttpMethod.Put, "members")

    /**
     * Remove a member from an organization.
     * The target userId is provided in the request body.
     * Route: DELETE /membership/members/{orgId}
     */
    val removeMember = operation<
        RemoveMemberNetworkRequest,
        NoQueryParam,
        OrganizationId,
        NoResponseBody
        >(HttpMethod.Delete, "members")

    /**
     * Leave an organization as the currently authenticated user.
     * Route: POST /membership/leave/{orgId}
     */
    val leaveOrganization = operation<
        NoRequestBody,
        NoQueryParam,
        OrganizationId,
        NoResponseBody
        >(HttpMethod.Post, "leave")

    /**
     * Transfer organization ownership to another member.
     * The target new owner userId is provided in the request body.
     * Route: POST /membership/transfer/{orgId}
     */
    val transferOwnership = operation<
        TransferOwnershipNetworkRequest,
        NoQueryParam,
        OrganizationId,
        NoResponseBody
        >(HttpMethod.Post, "transfer")

    /**
     * List all pending (non-expired, non-cancelled, non-accepted) invites for an organization.
     * Route: GET /membership/invites/{orgId}
     */
    val listPendingInvites = operation<
        NoRequestBody,
        NoQueryParam,
        OrganizationId,
        InviteListNetworkResponse
        >(HttpMethod.Get, "invites")

    /**
     * Cancel a pending invite.
     * Route: DELETE /membership/invite/{inviteId}
     */
    val cancelInvite = operation<
        NoRequestBody,
        NoQueryParam,
        InviteId,
        NoResponseBody
        >(HttpMethod.Delete, "invite")

    /**
     * Resend a pending invite with a regenerated code and reset expiry.
     * Route: POST /membership/invite/resend/{inviteId}
     */
    val resendInvite = operation<
        NoRequestBody,
        NoQueryParam,
        InviteId,
        NoResponseBody
        >(HttpMethod.Post, "invite/resend")

    /**
     * Join an organization via an invite code.
     * Does not require org membership — only a valid auth token.
     * Route: POST /membership/join
     */
    val joinViaCode = operation<
        JoinViaCodeNetworkRequest,
        NoQueryParam,
        NoPathParam,
        NoResponseBody
        >(HttpMethod.Post, "join")
}
