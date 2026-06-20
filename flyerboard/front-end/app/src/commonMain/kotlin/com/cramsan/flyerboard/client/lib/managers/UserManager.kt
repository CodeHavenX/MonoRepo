package com.cramsan.flyerboard.client.lib.managers

import com.cramsan.flyerboard.client.lib.models.UserModel
import com.cramsan.flyerboard.client.lib.service.UserService
import com.cramsan.framework.annotations.FrontendManager
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager to handle user operations.
 */
@FrontendManager
class UserManager(private val dependencies: ManagerDependencies, private val userService: UserService) {
    /**
     * Create a new user.
     */
    suspend fun createUser(
        firstName: String,
        lastName: String,
    ): Result<UserModel> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "createUser")
            userService.createUser(firstName, lastName).getOrThrow()
        }

    /**
     * Retrieve the currently authenticated user, including their role.
     */
    suspend fun getCurrentUser(): Result<UserModel> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "getCurrentUser")
            userService.getCurrentUser().getOrThrow()
        }

    companion object {
        private const val TAG = "UserManager"
    }
}
