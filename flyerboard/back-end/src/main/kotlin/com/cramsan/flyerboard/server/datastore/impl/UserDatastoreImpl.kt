package com.cramsan.flyerboard.server.datastore.impl

import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.utils.uuid.UUID
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.server.datastore.UserDatastore
import com.cramsan.flyerboard.server.service.models.User
import kotlinx.coroutines.delay

/**
 * Implementation of [UserDatastore] that provides user-related data operations.
 */
class UserDatastoreImpl : UserDatastore {
    override suspend fun createUser(
        firstName: String,
        lastName: String
    ): Result<User> = runSuspendCatching(TAG) {
        @Suppress("MagicNumber")
        delay(2000) // Simulate network/database delay
        User(
            id = UserId(UUID.random()),
            firstName = firstName,
            lastName = lastName
        )
    }

    companion object {
        private const val TAG = "UserDatastoreImpl"
    }
}
