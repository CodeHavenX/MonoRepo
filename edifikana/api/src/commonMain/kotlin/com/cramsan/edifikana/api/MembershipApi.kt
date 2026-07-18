package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.network.invite.InviteListNetworkResponse
import com.cramsan.edifikana.lib.model.network.invite.InviteMemberNetworkRequest
import com.cramsan.edifikana.lib.model.network.invite.JoinViaCodeNetworkRequest
import com.cramsan.edifikana.lib.model.network.organization.MemberListNetworkResponse
import com.cramsan.edifikana.lib.model.network.organization.RemoveMemberNetworkRequest
import com.cramsan.edifikana.lib.model.network.organization.TransferOwnershipNetworkRequest
import com.cramsan.edifikana.lib.model.network.organization.UpdateRoleNetworkRequest
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for organization membership management operations.
 *
 * Routes are structured as /membership/{subPath}/{pathParam} to fit the
 * single-path-parameter constraint of the API framework.
 */

object MembershipApi : Api("membership") {
    /**
     * Invite a new member to the organization.
     * Route: POST /membership/invite/{orgId}
     */
    val inviteMember =
        operation<
            InviteMemberNetworkRequest,
            NoQueryParam,
            OrganizationId,
            NoResponseBody,
            >(
            method = HttpMethod.Post,
            path = "invite",
            summary = "Invite a member",
            description = "Invites a new member to join an organization by email. Requires the ADMIN role or higher.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No organization exists for the given id."
            },
        )

    /**
     * List all active members of an organization.
     * Route: GET /membership/members/{orgId}
     */
    val listMembers =
        operation<
            NoRequestBody,
            NoQueryParam,
            OrganizationId,
            MemberListNetworkResponse,
            >(
            method = HttpMethod.Get,
            path = "members",
            summary = "List members",
            description = "Lists all active members of an organization.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No organization exists for the given id."
            },
        )

    /**
     * Update a member's role within an organization.
     * The target userId and new role are provided in the request body.
     * Route: PUT /membership/members/{orgId}
     */
    val updateMemberRole =
        operation<
            UpdateRoleNetworkRequest,
            NoQueryParam,
            OrganizationId,
            NoResponseBody,
            >(
            method = HttpMethod.Put,
            path = "members",
            summary = "Update a member's role",
            description = "Updates the role of an existing organization member. Requires the ADMIN role or higher.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No organization or member exists for the given ids."
            },
        )

    /**
     * Remove a member from an organization.
     * The target userId is provided in the request body.
     * Route: DELETE /membership/members/{orgId}
     */
    val removeMember =
        operation<
            RemoveMemberNetworkRequest,
            NoQueryParam,
            OrganizationId,
            NoResponseBody,
            >(
            method = HttpMethod.Delete,
            path = "members",
            summary = "Remove a member",
            description = "Removes a member from an organization. Requires the ADMIN role or higher.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No organization or member exists for the given ids."
            },
        )

    /**
     * Leave an organization as the currently authenticated user.
     * Route: POST /membership/leave/{orgId}
     */
    val leaveOrganization =
        operation<
            NoRequestBody,
            NoQueryParam,
            OrganizationId,
            NoResponseBody,
            >(
            method = HttpMethod.Post,
            path = "leave",
            summary = "Leave an organization",
            description = "Removes the authenticated user's own membership from an organization.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No organization exists for the given id."
            },
        )

    /**
     * Transfer organization ownership to another member.
     * The target new owner userId is provided in the request body.
     * Route: POST /membership/transfer/{orgId}
     */
    val transferOwnership =
        operation<
            TransferOwnershipNetworkRequest,
            NoQueryParam,
            OrganizationId,
            NoResponseBody,
            >(
            method = HttpMethod.Post,
            path = "transfer",
            summary = "Transfer ownership",
            description = "Transfers ownership of an organization to another member. Requires the OWNER role.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No organization or target member exists for the given ids."
            },
        )

    /**
     * List all pending (non-expired, non-cancelled, non-accepted) invites for an organization.
     * Route: GET /membership/invites/{orgId}
     */
    val listPendingInvites =
        operation<
            NoRequestBody,
            NoQueryParam,
            OrganizationId,
            InviteListNetworkResponse,
            >(
            method = HttpMethod.Get,
            path = "invites",
            summary = "List pending invites",
            description = "Lists all pending (non-expired, non-cancelled, non-accepted) invites for an organization.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No organization exists for the given id."
            },
        )

    /**
     * Cancel a pending invite.
     * Route: DELETE /membership/invite/{inviteId}
     */
    val cancelInvite =
        operation<
            NoRequestBody,
            NoQueryParam,
            InviteId,
            NoResponseBody,
            >(
            method = HttpMethod.Delete,
            path = "invite",
            summary = "Cancel an invite",
            description = "Cancels a pending invite by its identifier.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No invite exists for the given id."
            },
        )

    /**
     * Resend a pending invite with a regenerated code and reset expiry.
     * Route: POST /membership/invite/resend/{inviteId}
     */
    val resendInvite =
        operation<
            NoRequestBody,
            NoQueryParam,
            InviteId,
            NoResponseBody,
            >(
            method = HttpMethod.Post,
            path = "invite/resend",
            summary = "Resend an invite",
            description = "Resends a pending invite with a regenerated code and a reset expiry.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No invite exists for the given id."
            },
        )

    /**
     * Join an organization via an invite code.
     * Does not require org membership — only a valid auth token.
     * Route: POST /membership/join
     */
    val joinViaCode =
        operation<
            JoinViaCodeNetworkRequest,
            NoQueryParam,
            NoPathParam,
            NoResponseBody,
            >(
            method = HttpMethod.Post,
            path = "join",
            summary = "Join via invite code",
            description =
            "Joins an organization using a valid invite code. Requires an authenticated user, " +
                "but not existing org membership.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No invite exists for the given code, or it has expired."
            },
        )
}
