package com.cramsan.flyerboard.client.lib.service

import com.cramsan.flyerboard.client.lib.models.UserModel
import com.cramsan.framework.annotations.FrontendService

/**
 * Service to manage users.
 */
@FrontendService
interface UserService {
    /**
     * Create a new user.
     */
    suspend fun createUser(
        firstName: String,
        lastName: String,
    ): Result<UserModel>

    /**
     * Retrieve the currently authenticated user, including their role.
     */
    suspend fun getCurrentUser(): Result<UserModel>
}
