package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.CreateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.GetAllUsersQueryParams
import com.cramsan.edifikana.lib.model.network.InviteNetworkResponse
import com.cramsan.edifikana.lib.model.network.InviteUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdatePasswordNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * Singleton object representing the User API with its operations.
 */
object UserApi : Api("user") {

    val createUser = operationNoArg<CreateUserNetworkRequest, Unit, UserNetworkResponse>(HttpMethod.Post)

    val getUser = operationWithArg<Unit, Unit, UserNetworkResponse>(HttpMethod.Get)

    val updatePassword = operationNoArg<UpdatePasswordNetworkRequest, Unit, Unit>(HttpMethod.Put, "password")

    val getAllUsers = operationNoArg<Unit, GetAllUsersQueryParams, List<UserNetworkResponse>>(
        HttpMethod.Get
    )

    val updateUser = operationWithArg<UpdateUserNetworkRequest, Unit, UserNetworkResponse>(HttpMethod.Put)

    val deleteUser = operationWithArg<Unit, Unit, Unit>(HttpMethod.Delete)

    val associateUser = operationNoArg<Unit, Unit, UserNetworkResponse>(HttpMethod.Post, "associate")

    val inviteUser = operationNoArg<InviteUserNetworkRequest, Unit, Unit>(HttpMethod.Post, "invite")

    val getInvites = operationWithArg<Unit, Unit, List<InviteNetworkResponse>>(HttpMethod.Get, "invites")
}
