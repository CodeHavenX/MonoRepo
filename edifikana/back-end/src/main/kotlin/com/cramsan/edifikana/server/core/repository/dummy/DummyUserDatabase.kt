@file:Suppress("MagicNumber")

package com.cramsan.edifikana.server.core.repository.dummy

import com.cramsan.edifikana.server.core.repository.UserDatabase
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.UserId
import com.cramsan.edifikana.server.core.service.models.requests.CreateUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetUserRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateUserRequest
import kotlinx.coroutines.delay

/**
 * Dummy implementation of [UserDatabase].
 */
class DummyUserDatabase : UserDatabase {
    override suspend fun createUser(request: CreateUserRequest): Result<User> {
        delay(1000)
        return Result.success(
            User(
                id = UserId("1"),
                email = "test@test.com",
            )
        )
    }

    override suspend fun getUser(request: GetUserRequest): Result<User?> {
        delay(1000)
        return Result.success(
            User(
                id = UserId("1"),
                email = "test@test.com",
            )
        )
    }

    override suspend fun getUsers(): Result<List<User>> {
        delay(1000)
        return Result.success(
            (0..10).map {
                User(
                    id = UserId(it.toString()),
                    email = "test-$it@test.com",
                )
            }
        )
    }

    override suspend fun updateUser(request: UpdateUserRequest): Result<User> {
        delay(1000)
        return Result.success(
            User(
                id = UserId("1"),
                email = "test@test.com",
            )
        )
    }

    override suspend fun deleteUser(request: DeleteUserRequest): Result<Boolean> {
        delay(1000)
        return Result.success(true)
    }
}
