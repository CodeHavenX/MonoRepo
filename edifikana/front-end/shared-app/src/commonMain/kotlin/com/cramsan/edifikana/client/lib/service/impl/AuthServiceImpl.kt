package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.lib.CHECK_GLOBAL_PERMS
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
import io.ktor.client.request.parameter
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

    /**
     * Check if the user is signed in.
     */
    override suspend fun isSignedIn(): Result<Boolean> = runSuspendCatching(TAG) {
        auth.awaitInitialization()
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

    /**
     * Get the signed-in user from the server.
     */
    @OptIn(NetworkModel::class)
    override suspend fun getUser(
        checkGlobalPerms: Boolean,
    ): Result<UserModel> = runSuspendCatching(TAG) {
        val userId = auth.currentUserOrNull()?.id ?: error("User not signed in")
        val response = http.get("${Routes.User.PATH}/$userId") {
            parameter(CHECK_GLOBAL_PERMS, checkGlobalPerms)
        }.body<UserNetworkResponse>()
        val userModel = response.toUserModel()
        _activeUser.value = userModel.id
        userModel
    }

    /**
     * Signs in the user with the given email and password.
     */
    override suspend fun signIn(email: String, password: String): Result<UserModel> = runSuspendCatching(TAG) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        getUser().getOrThrow()
    }

    /**
     * Signs the user out of the application.
     */
    override suspend fun signOut() = runSuspendCatching(TAG) {
        auth.signOut()
        _activeUser.value = null
    }

    /**
     * Get the active user as an observable flow.
     */
    override fun activeUser(): StateFlow<UserId?> {
        return _activeUser.asStateFlow()
    }

    /**
     * Signs up the user with the given email, phone number, password, first name and last name.
     */
    @OptIn(NetworkModel::class)
    override suspend fun signUp(
        email: String,
        phoneNumber: String,
        password: String,
        firstName: String,
        lastName: String,
    ): Result<UserModel> = runSuspendCatching(TAG) {
        val response = http.post(Routes.User.PATH) {
            setBody(
                CreateUserNetworkRequest(
                    email = email,
                    phoneNumber = phoneNumber,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                )
            )
            contentType(ContentType.Application.Json)
        }.body<UserNetworkResponse>()
        val userModel = response.toUserModel()
        _activeUser.value = userModel.id
        userModel
    }

    /**
     * Resets the password for the user with the given email or phone number.
     */
    override suspend fun passwordReset(email: String?, phoneNumber: String?): Result<Unit> = runSuspendCatching(TAG) {
        TODO("Implement functionality to reset password and authenticate user.")
    }

    /**
     * Verifies if the user has the required permissions to access the service.
     */
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

    /**
     * Static tag for logging
     */
    companion object {
        private const val TAG = "AuthServiceImpl"
    }
}
