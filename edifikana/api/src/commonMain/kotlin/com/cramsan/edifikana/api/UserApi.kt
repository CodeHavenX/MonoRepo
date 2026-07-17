package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.network.invite.InviteListNetworkResponse
import com.cramsan.edifikana.lib.model.network.invite.InviteUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.password.PasswordResetNetworkRequest
import com.cramsan.edifikana.lib.model.network.user.CheckUserNetworkResponse
import com.cramsan.edifikana.lib.model.network.user.CreateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.user.GetAllUsersQueryParams
import com.cramsan.edifikana.lib.model.network.user.UpdateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.user.UserEmailQueryParam
import com.cramsan.edifikana.lib.model.network.user.UserListNetworkResponse
import com.cramsan.edifikana.lib.model.network.user.UserNetworkResponse
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
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
 * Singleton object representing the User API with its operations.
 */

object UserApi : Api("user") {
    val createUser =
        publicOperation<
            CreateUserNetworkRequest,
            NoQueryParam,
            NoPathParam,
            UserNetworkResponse,
            >(
            method = HttpMethod.Post,
            summary = "Create a user",
            description = "Registers a new user account.",
            responses =
            AdditionalResponses {
                HttpStatusCode.Conflict describedAs "A user with this email already exists."
            },
        )

    val getUser =
        operation<
            NoRequestBody,
            NoQueryParam,
            UserId,
            UserNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "Get a user",
            description = "Retrieves a single user by their identifier.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No user exists for the given id."
            },
        )

    val getAllUsers =
        operation<
            NoRequestBody,
            GetAllUsersQueryParams,
            NoPathParam,
            UserListNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "List users",
            description = "Lists all users belonging to an organization.",
            responses = UniversalResponsesOnly,
        )

    val updateUser =
        operation<
            UpdateUserNetworkRequest,
            NoQueryParam,
            UserId,
            UserNetworkResponse,
            >(
            method = HttpMethod.Put,
            summary = "Update a user",
            description = "Updates the mutable fields of an existing user.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No user exists for the given id."
            },
        )

    val deleteUser =
        operation<
            NoRequestBody,
            NoQueryParam,
            UserId,
            NoResponseBody,
            >(
            method = HttpMethod.Delete,
            summary = "Delete a user",
            description = "Permanently deletes a user by their identifier.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No user exists for the given id."
            },
        )

    val associateUser =
        operation<
            NoRequestBody,
            NoQueryParam,
            NoPathParam,
            UserNetworkResponse,
            >(
            method = HttpMethod.Post,
            path = "associate",
            summary = "Associate the authenticated user",
            description = "Associates the authenticated auth identity with its corresponding user record.",
            responses = UniversalResponsesOnly,
        )

    val inviteUser =
        operation<
            InviteUserNetworkRequest,
            NoQueryParam,
            NoPathParam,
            NoResponseBody,
            >(
            method = HttpMethod.Post,
            path = "invite",
            summary = "Invite a user",
            description = "Invites a user to join an organization by email.",
            responses = UniversalResponsesOnly,
        )

    val getInvites =
        operation<
            NoRequestBody,
            NoQueryParam,
            OrganizationId,
            InviteListNetworkResponse,
            >(
            method = HttpMethod.Get,
            path = "invites",
            summary = "List invites for an organization",
            description = "Lists pending invites for an organization.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No organization exists for the given id."
            },
        )

    /**
     * Accept a pending invitation.
     * Route: POST /user/invite/accept/{inviteId}
     */
    val acceptInvite =
        operation<
            NoRequestBody,
            NoQueryParam,
            InviteId,
            NoResponseBody,
            >(
            method = HttpMethod.Post,
            path = "invite/accept",
            summary = "Accept an invite",
            description = "Accepts a pending invitation as the authenticated user.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No invite exists for the given id."
            },
        )

    /**
     * Decline a pending invitation.
     * Route: POST /user/invite/decline/{inviteId}
     */
    val declineInvite =
        operation<
            NoRequestBody,
            NoQueryParam,
            InviteId,
            NoResponseBody,
            >(
            method = HttpMethod.Post,
            path = "invite/decline",
            summary = "Decline an invite",
            description = "Declines a pending invitation as the authenticated user.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No invite exists for the given id."
                HttpStatusCode.Forbidden describedAs "The invite is not addressed to the caller's email address."
            },
        )

    /**
     * Cancel a pending invite (manager action).
     * Route: DELETE /user/invites/{inviteId}
     */
    val cancelInvite =
        operation<
            NoRequestBody,
            NoQueryParam,
            InviteId,
            NoResponseBody,
            >(
            method = HttpMethod.Delete,
            path = "invites",
            summary = "Cancel an invite",
            description = "Cancels a pending invite. Requires the ADMIN role or higher.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No invite exists for the given id."
            },
        )

    val checkUserExists =
        publicOperation<
            NoRequestBody,
            UserEmailQueryParam,
            NoPathParam,
            CheckUserNetworkResponse,
            >(
            method = HttpMethod.Get,
            path = "checkUser",
            summary = "Check if a user exists",
            description = "Checks whether a user is already registered for the given email address.",
            responses = UniversalResponsesOnly,
        )

    /**
     * Request a password reset email for the given email address.
     * Route: POST /user/request-password-reset
     * Always returns 200 regardless of whether the email exists.
     */
    val requestPasswordReset =
        publicOperation<
            PasswordResetNetworkRequest,
            NoQueryParam,
            NoPathParam,
            NoResponseBody,
            >(
            method = HttpMethod.Post,
            path = "request-password-reset",
            summary = "Request a password reset",
            description =
            "Sends a password reset email or SMS for the given address. Always returns 200 " +
                "regardless of whether the email or phone number exists, to avoid leaking account existence.",
            responses = UniversalResponsesOnly,
        )
}
