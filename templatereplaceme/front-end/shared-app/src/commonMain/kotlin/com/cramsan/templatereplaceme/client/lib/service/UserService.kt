package com.cramsan.templatereplaceme.client.lib.service

import com.cramsan.framework.annotations.FrontendService
import com.cramsan.templatereplaceme.client.lib.models.UserModel

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
}
