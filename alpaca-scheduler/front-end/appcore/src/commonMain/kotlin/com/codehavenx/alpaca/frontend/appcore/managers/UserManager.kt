package com.codehavenx.alpaca.frontend.appcore.managers

import com.codehavenx.alpaca.frontend.appcore.models.User
import com.codehavenx.alpaca.frontend.appcore.service.UserService
import com.codehavenx.alpaca.frontend.appcore.utils.getOrCatch
import com.cramsan.framework.logging.logI

/**
 * Manager to perform operations on users.
 */
class UserManager(
    private val userService: UserService,
    private val workContext: WorkContext,
) {

    /**
     * Get the list of users.
     */
    suspend fun getUsers(): Result<List<User>> = workContext.getOrCatch(TAG) {
        logI(TAG, "getUsers")
        userService.getUsers().getOrThrow()
    }

    companion object {
        private const val TAG = "UserManager"
    }
}
