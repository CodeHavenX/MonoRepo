package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.api.UserApi
import com.cramsan.edifikana.client.lib.models.Invite
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.CreateUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.GetAllUsersQueryParams
import com.cramsan.edifikana.lib.model.network.InviteNetworkResponse
import com.cramsan.edifikana.lib.model.network.InviteUserNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdatePasswordNetworkRequest
import com.cramsan.edifikana.lib.utils.requireSuccess
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.assertlib.assertFalse
import com.cramsan.framework.core.Hashing
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logD
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logW
import com.cramsan.framework.networkapi.buildRequest
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.HttpClient
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
        val response = UserApi.getUser.buildRequest(
            argument = userId,
        ).execute(http)
        val userModel = response.toUserModel()
        _activeUser.value = userModel.id
        userModel
    }

    @OptIn(NetworkModel::class)
    override suspend fun getUsersByOrganization(
        organizationId: OrganizationId,
    ): Result<List<UserModel>> = runSuspendCatching(TAG) {
        val response = UserApi.getAllUsers
            .buildRequest(GetAllUsersQueryParams(organizationId))
            .execute(http)
        val userModels = response.map { it.toUserModel() }
        userModels
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
        val response = UserApi.createUser.buildRequest(
            CreateUserNetworkRequest(
                email = email,
                phoneNumber = phoneNumber,
                firstName = firstName,
                lastName = lastName,
                password = null, // Password is not required for sign-up, but can be set later.
            ),
        ).execute(http)
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
        try {
            auth.verifyEmailOtp(OtpType.Email.EMAIL, email, hashToken)
        } catch (e: AuthRestException) {
            logE(TAG, "Error verifying OTP", e)
            throw ClientRequestExceptions.UnauthorizedException("ERROR: Invalid OTP code.")
        }

        if (createUser) {
            try {
                UserApi.associateUser.buildRequest().execute(http)
            } catch (e: ClientRequestExceptions.ConflictException) {
                logW(TAG, "User already exists, not creating a new user.", e)
            }
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

    @OptIn(NetworkModel::class, SecureStringAccess::class)
    override suspend fun changePassword(
        currentPassword: SecureString,
        newPassword: SecureString
    ): Result<Unit> = runSuspendCatching(TAG) {
        val email = getUser().requireSuccess().email

        val hashedCurrentPassword = Hashing.insecureHash(currentPassword.reveal().encodeToByteArray()).toString()

        UserApi.updatePassword.buildRequest(
            UpdatePasswordNetworkRequest(
                currentPasswordHashed = hashedCurrentPassword,
                newPassword = newPassword.reveal()
            ),
        ).execute(http)

        try {
            auth.signInWith(Email) {
                this.email = email
                this.password = newPassword.reveal()
            }
        } catch (e: AuthRestException) {
            logE(TAG, "Error signing in after changing password", e)
            throw ClientRequestExceptions.UnauthorizedException("ERROR: Invalid credentials after changing password.")
        }
    }

    @OptIn(NetworkModel::class)
    override suspend fun inviteStaff(email: String, organizationId: OrganizationId): Result<Unit> = runSuspendCatching(
        TAG
    ) {
        UserApi.inviteUser.buildRequest(
            InviteUserNetworkRequest(
                email = email,
                organizationId = organizationId
            ),
        ).execute(http)
    }

    @OptIn(NetworkModel::class)
    override suspend fun getInvites(organizationId: OrganizationId): Result<List<Invite>> = runSuspendCatching(TAG) {
        val response = UserApi.getInvites.buildRequest(
            argument = organizationId.id,
        ).execute(http)
        val invites = response.map { it.toInvite() }
        invites
    }

    companion object {
        private const val TAG = "AuthServiceImpl"
    }
}

@OptIn(NetworkModel::class)
private fun InviteNetworkResponse.toInvite(): Invite {
    return Invite(
        id = this.inviteId,
        email = this.email,
    )
}
