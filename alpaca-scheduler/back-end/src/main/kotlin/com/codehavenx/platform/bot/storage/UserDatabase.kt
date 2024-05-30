package com.codehavenx.platform.bot.storage

import com.codehavenx.platform.bot.domain.models.User
import com.codehavenx.platform.bot.domain.models.UserId
import com.codehavenx.platform.bot.storage.entity.UserEntity
import com.cramsan.framework.logging.logD
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

class UserDatabase(
    private val database: MongoDatabase,
    private val objectIdProvider: () -> ObjectId,
) {

    private val collection = database.getCollection<UserEntity>(COLLECTION_NAME)

    suspend fun createUser(
        name: String,
    ): User {
        logD(TAG, "Creating user: %S", name)
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
    }

    suspend fun getUser(userId: UserId): User? {
        logD(TAG, "Getting user: %S", userId)
        return database.getCollection<UserEntity>(COLLECTION_NAME)
            .find(Filters.eq("_id", ObjectId(userId.userId)))
            .firstOrNull()?.toUser()
    }

    suspend fun updateUser(user: User): Boolean {
        logD(TAG, "Updating user: %S", user.userId)
        val userEntity = user.toUserEntity()
        return database.getCollection<UserEntity>(COLLECTION_NAME).replaceOne(
            Filters.eq("_id", userEntity.id),
            userEntity,
        ).wasAcknowledged()
    }

    suspend fun deleteUser(userId: UserId): Boolean {
        logD(TAG, "Deleting user: %S", userId)
        return database.getCollection<UserEntity>(COLLECTION_NAME).deleteOne(
            Filters.eq("_id", ObjectId(userId.userId)),
        ).wasAcknowledged()
    }

    companion object {
        private const val TAG = "UserDatabase"
        private const val COLLECTION_NAME = "Users"
    }
}
