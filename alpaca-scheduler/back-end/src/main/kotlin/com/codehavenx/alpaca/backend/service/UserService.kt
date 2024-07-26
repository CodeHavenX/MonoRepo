package com.codehavenx.alpaca.backend.service

import com.codehavenx.alpaca.backend.models.User
import com.codehavenx.alpaca.backend.models.UserId
import com.codehavenx.alpaca.backend.storage.UserDatabase

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
        @Suppress("UnusedPrivateProperty")
        private const val TAG = "UserService"
    }
}
