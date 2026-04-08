package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.lib.model.network.user.CheckUserNetworkResponse
import com.cramsan.edifikana.lib.model.network.user.CreateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.password.PasswordResetNetworkRequest
import com.cramsan.edifikana.lib.model.network.user.GetAllUsersQueryParams
import com.cramsan.edifikana.lib.model.network.invite.InviteListNetworkResponse
import com.cramsan.edifikana.lib.model.network.invite.InviteUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.user.UpdateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.user.UserEmailQueryParam
import com.cramsan.edifikana.lib.model.network.user.UserListNetworkResponse
import com.cramsan.edifikana.lib.model.network.user.UserNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * Singleton object representing the User API with its operations.
 */
@OptIn(NetworkModel::class)
object UserApi : Api("user") {

    val createUser =
        operation<
            CreateUserNetworkRequest,
            NoQueryParam,
            NoPathParam,
            UserNetworkResponse
            >(HttpMethod.Post)

    val getUser = operation<
        NoRequestBody,
        NoQueryParam,
        UserId,
        UserNetworkResponse
        >(HttpMethod.Get)

    val getAllUsers = operation<
        NoRequestBody,
        GetAllUsersQueryParams,
        NoPathParam,
        UserListNetworkResponse
        >(
        HttpMethod.Get
    )

    val updateUser = operation<
        UpdateUserNetworkRequest,
        NoQueryParam,
        UserId,
        UserNetworkResponse
        >(HttpMethod.Put)

    val deleteUser = operation<
        NoRequestBody,
        NoQueryParam,
        UserId,
        NoResponseBody
        >(HttpMethod.Delete)

    val associateUser = operation<
        NoRequestBody,
        NoQueryParam,
        NoPathParam,
        UserNetworkResponse
        >(HttpMethod.Post, "associate")

    val inviteUser = operation<
        InviteUserNetworkRequest,
        NoQueryParam,
        NoPathParam,
        NoResponseBody
        >(HttpMethod.Post, "invite")

    val getInvites = operation<
        NoRequestBody,
        NoQueryParam,
        OrganizationId,
        InviteListNetworkResponse
        >(
        HttpMethod.Get,
        "invites"
    )

    /**
     * Accept a pending invitation.
     * Route: POST /user/invite/accept/{inviteId}
     */
    val acceptInvite = operation<
        NoRequestBody,
        NoQueryParam,
        InviteId,
        NoResponseBody
        >(
        HttpMethod.Post,
        "invite/accept"
    )

    /**
     * Decline a pending invitation.
     * Route: POST /user/invite/decline/{inviteId}
     */
    val declineInvite = operation<
        NoRequestBody,
        NoQueryParam,
        InviteId,
        NoResponseBody
        >(
        HttpMethod.Post,
        "invite/decline"
    )

    /**
     * Cancel a pending invite (manager action).
     * Route: DELETE /user/invites/{inviteId}
     */
    val cancelInvite = operation<
        NoRequestBody,
        NoQueryParam,
        InviteId,
        NoResponseBody
        >(
        HttpMethod.Delete,
        "invites"
    )

    val checkUserExists = operation<
        NoRequestBody,
        UserEmailQueryParam,
        NoPathParam,
        CheckUserNetworkResponse
        >(
        HttpMethod.Get,
        "checkUser"
    )

    /**
     * Request a password reset email for the given email address.
     * Route: POST /user/request-password-reset
     * Always returns 200 regardless of whether the email exists.
     */
    val requestPasswordReset = operation<
        PasswordResetNetworkRequest,
        NoQueryParam,
        NoPathParam,
        NoResponseBody
        >(HttpMethod.Post, "request-password-reset")
}
