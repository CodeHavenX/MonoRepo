package com.codehavenx.alpaca.backend.storage

import com.codehavenx.alpaca.backend.models.User
import com.codehavenx.alpaca.backend.models.UserId
import com.cramsan.framework.logging.logD

@Suppress("UnusedParameter", "UnusedPrivateProperty", "RedundantSuspendModifier")
class UserDatabase {

    suspend fun createUser(
        name: String,
    ): User {
        logD(TAG, "Creating user: %S", name)
        /*
        val userEntity = UserEntity(
            id = objectIdProvider(),
            name = name,
        )

        val result = collection.insertOne(userEntity)
        logD(TAG, "User %S, created = %S", userEntity.id, result.wasAcknowledged())
        if (result.wasAcknowledged()) {
            return userEntity.toUser()
        } else {
            TODO()
        }
         */
        TODO()
    }

    suspend fun getUser(userId: UserId): User? {
        logD(TAG, "Getting user: %S", userId)
        /*
        return database.getCollection<UserEntity>(COLLECTION_NAME)
            .find(Filters.eq("_id", ObjectId(userId.userId)))
            .firstOrNull()?.toUser()

         */
        TODO()
    }

    suspend fun updateUser(user: User): Boolean {
        logD(TAG, "Updating user: %S", user.userId)
        val userEntity = user.toUserEntity()
        /*
        return database.getCollection<UserEntity>(COLLECTION_NAME).replaceOne(
            Filters.eq("_id", userEntity.id),
            userEntity,
        ).wasAcknowledged()
         */
        TODO()
    }

    suspend fun deleteUser(userId: UserId): Boolean {
        logD(TAG, "Deleting user: %S", userId)
        /*
        return database.getCollection<UserEntity>(COLLECTION_NAME).deleteOne(
            Filters.eq("_id", ObjectId(userId.userId)),
        ).wasAcknowledged()
         */
        TODO()
    }

    companion object {
        private const val TAG = "UserDatabase"
        private const val COLLECTION_NAME = "Users"
    }
}
