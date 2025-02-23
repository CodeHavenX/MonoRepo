package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.CreateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.framework.assertlib.assertFalse
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Default implementation for the [AuthService].
 */
class AuthServiceImpl(
    private val auth: Auth,
    private val http: HttpClient,
) : AuthService {

    private val _activeUser = MutableStateFlow<UserId?>(null)

    override suspend fun isSignedIn(): Result<Boolean> = runSuspendCatching(TAG) {
        val user = auth.currentUserOrNull()
        _activeUser.value = user?.id?.let { UserId(it) }
        if (user == null) {
            logD(TAG, "User not signed in")
            false
        } else {
            try {
                auth.refreshCurrentSession()
                true
            } catch (e: RestException) {
                logE(TAG, "Failed to refresh session. User considered signed out.", e)
                false
            }
        }
    }

    @OptIn(NetworkModel::class)
    override suspend fun getUser(): Result<UserModel> = runSuspendCatching(TAG) {
        val userId = auth.currentUserOrNull()?.id ?: error("User not signed in")
        val response = http.get("${Routes.User.PATH}/$userId")
            .body<UserNetworkResponse>()
        val userModel = response.toUserModel()
        _activeUser.value = userModel.id
        userModel
    }

    override suspend fun signIn(email: String, password: String): Result<UserModel> = runSuspendCatching(TAG) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        getUser().getOrThrow()
    }

    override suspend fun signOut() = runSuspendCatching(TAG) {
        auth.signOut()
        _activeUser.value = null
    }

    override fun activeUser(): StateFlow<UserId?> {
        return _activeUser.asStateFlow()
    }

    @OptIn(NetworkModel::class)
    override suspend fun signUp(
        username: String,
        password: String,
        fullname: String,
    ): Result<UserModel> = runSuspendCatching(TAG) {
        val response = http.post(Routes.User.PATH) {
            setBody(
                CreateUserNetworkRequest(
                    username = username,
                    password = password,
                    fullname = fullname,
                )
            )
            contentType(ContentType.Application.Json)
        }.body<UserNetworkResponse>()
        val userModel = response.toUserModel()
        _activeUser.value = userModel.id
        userModel
    }

    override suspend fun verifyPermissions(): Result<Boolean> {
        // call the admin API. If the call succeeds, the user then we have the wrong credentials.
        val hasServicePermissions = try {
            auth.admin.retrieveUsers()
            true
        } catch (_: AuthRestException) {
            // This exception is expected, it means the user does not have admin permissions.
            false
        }
        assertFalse(hasServicePermissions, TAG, "User has admin permissions, this is not allowed!")

        return Result.success(!hasServicePermissions)
    }

    companion object {
        private const val TAG = "AuthServiceImpl"
    }
}
