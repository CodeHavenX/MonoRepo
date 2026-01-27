package com.cramsan.templatereplaceme.client.lib.managers

import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI
import com.cramsan.templatereplaceme.client.lib.models.UserModel
import com.cramsan.templatereplaceme.client.lib.service.UserService

/**
 * Manager to handle user operations.
 */
class UserManager(private val dependencies: ManagerDependencies, private val authService: UserService) {

    /**
     * Create a new user.
     */
    suspend fun createUser(firstName: String, lastName: String): Result<UserModel> = dependencies.getOrCatch(TAG) {
        logI(TAG, "signing in with OTP code")
        authService.createUser(firstName, lastName).getOrThrow()
    }

    companion object {
        private const val TAG = "UserManager"
    }
}
