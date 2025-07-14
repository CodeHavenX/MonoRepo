package com.cramsan.edifikana.server.core.repository.dummy

import com.cramsan.edifikana.server.core.repository.UserDatabase
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.requests.AssociateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePasswordRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateUserRequest
import com.cramsan.framework.logging.logD

/**
 * Class with dummy data to be used only for development and testing.
 */
class DummyUserDatabase : UserDatabase {
    override suspend fun createUser(request: CreateUserRequest): Result<User> {
        logD(TAG, "createUser")
        return Result.success(USER_1)
    }

    override suspend fun associateUser(request: AssociateUserRequest): Result<User> {
        logD(TAG, "associateUser")
        return Result.success(USER_1)
    }

    override suspend fun getUser(request: GetUserRequest): Result<User?> {
        logD(TAG, "getUser")
        return Result.success(USER_1)
    }

    override suspend fun getUsers(): Result<List<User>> {
        logD(TAG, "getUsers")
        return Result.success(listOf(USER_1, USER_2, USER_3, USER_4))
    }

    override suspend fun updateUser(request: UpdateUserRequest): Result<User> {
        logD(TAG, "updateUser")
        return Result.success(USER_1)
    }

    override suspend fun deleteUser(request: DeleteUserRequest): Result<Boolean> {
        logD(TAG, "deleteUser")
        return Result.success(true)
    }

    override suspend fun updatePassword(request: UpdatePasswordRequest): Result<Boolean> {
        logD(TAG, "updatePassword")
        return Result.success(true)
    }

    companion object {
        private const val TAG = "DummyUserDatabase"
    }
}
