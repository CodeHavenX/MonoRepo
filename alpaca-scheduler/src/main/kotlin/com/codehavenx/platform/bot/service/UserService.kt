package com.codehavenx.platform.bot.service

import com.codehavenx.platform.bot.domain.models.User
import com.codehavenx.platform.bot.domain.models.UserId
import com.codehavenx.platform.bot.storage.UserDatabase

class UserService(
    private val userDatabase: UserDatabase,
) {

    suspend fun createUser(
        name: String,
    ): User {
        return userDatabase.createUser(
            name = name,
        )
    }

    suspend fun getUser(
        userId: UserId,
    ): User? {
        return userDatabase.getUser(userId)
    }

    suspend fun deleteEvent(
        userId: UserId,
    ): Boolean {
        return userDatabase.deleteUser(userId)
    }

    companion object {
        private const val TAG = "UserService"
    }
}
