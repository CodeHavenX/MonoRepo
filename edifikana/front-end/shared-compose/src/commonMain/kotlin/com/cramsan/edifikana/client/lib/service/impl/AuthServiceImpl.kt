package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.framework.core.runSuspendCatching
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
        user != null
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

    companion object {
        private const val TAG = "AuthServiceImpl"
    }
}
