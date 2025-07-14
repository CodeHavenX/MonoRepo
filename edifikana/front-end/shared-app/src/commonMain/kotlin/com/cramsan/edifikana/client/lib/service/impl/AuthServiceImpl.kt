package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.AssociateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.CreateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.framework.assertlib.assertFalse
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.OTP
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

    @OptIn(NetworkModel::class)
    override suspend fun getUser(): Result<UserModel> = runSuspendCatching(TAG) {
        val userId = auth.currentUserOrNull()?.id ?: error("User not signed in")
        val response = http.get("${Routes.User.PATH}/$userId") {
        }.body<UserNetworkResponse>()
        val userModel = response.toUserModel()
        _activeUser.value = userModel.id
        userModel
    }

    override suspend fun signInWithPassword(email: String, password: String): Result<UserModel> =
        runSuspendCatching(TAG) {
            try {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
            } catch (e: AuthRestException) {
                logE(TAG, "Error signing in", e)
                throw ClientRequestExceptions.UnauthorizedException("ERROR: Invalid credentials.")
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
        email: String,
        phoneNumber: String,
        firstName: String,
        lastName: String,
    ): Result<UserModel> = runSuspendCatching(TAG) {
        // The sign-in with OTP is used to create a user and send an OTP for verification.
        // https://supabase.com/docs/reference/kotlin/auth-signinwithotp
        auth.signInWith(OTP) {
            this.email = email
            createUser = true // This will create a user in Supabase Auth.
        }

        // Now we need to create the user in our system.
        val response = http.post(Routes.User.PATH) {
            setBody(
                CreateUserNetworkRequest(
                    email = email,
                    phoneNumber = phoneNumber,
                    firstName = firstName,
                    lastName = lastName,
                    password = null, // Password is not required for sign-up, but can be set later.
                )
            )
            contentType(ContentType.Application.Json)
        }.body<UserNetworkResponse>()
        val userModel = response.toUserModel()
        userModel
    }

    override suspend fun sendOtpEmail(email: String): Result<Unit> = runSuspendCatching(TAG) {
        auth.signInWith(OTP) {
            this.email = email
            createUser = false
        }
    }

    @OptIn(NetworkModel::class)
    override suspend fun signInWithOtp(
        email: String,
        hashToken: String,
        createUser: Boolean,
    ): Result<UserModel> = runSuspendCatching(
        TAG
    ) {
        auth.verifyEmailOtp(OtpType.Email.EMAIL, email, hashToken)

        if (createUser) {
            // After the OTP is verified, we create the user in our system.
            val response = http.post("${Routes.User.PATH}/associate") {
                setBody(AssociateUserNetworkRequest(email = email))
                contentType(ContentType.Application.Json)
            }.body<UserNetworkResponse>()
        }

        getUser().getOrThrow()
    }

    override suspend fun passwordReset(email: String?, phoneNumber: String?): Result<Unit> = runSuspendCatching(TAG) {
        TODO("Implement functionality to reset password and authenticate user.")
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

    override suspend fun updateUser(
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?
    ): Result<UserModel> {
        TODO()
    }

    companion object {
        private const val TAG = "AuthServiceImpl"
    }
}
