package com.codehavenx.alpaca.frontend.appcore.managers

import com.codehavenx.alpaca.frontend.appcore.models.User
import com.codehavenx.alpaca.frontend.appcore.service.UserService
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager to perform operations on users.
 */
class UserManager(
    private val userService: UserService,
    private val dependencies: ManagerDependencies,
) {

    /**
     * Get the list of users.
     */
    suspend fun getUsers(): Result<List<User>> = dependencies.getOrCatch(TAG) {
        logI(TAG, "getUsers")
        userService.getUsers().getOrThrow()
    }

    companion object {
        private const val TAG = "UserManager"
    }
}
