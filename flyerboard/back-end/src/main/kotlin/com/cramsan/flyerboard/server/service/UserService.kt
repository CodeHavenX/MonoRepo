package com.cramsan.flyerboard.server.service

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.framework.logging.logD
import com.cramsan.flyerboard.server.datastore.UserDatastore
import com.cramsan.flyerboard.server.service.models.User
import com.cramsan.flyerboard.server.settings.FlyerBoardSettingKey

/**
 * Example of service to manage users.
 */
class UserService(
    private val userDatastore: UserDatastore,
    private val settingsHolder: SettingsHolder,
) {

    /**
     * Creates a new user with the given first and last name.
     *
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @return A [Result] containing the created [User] or an error if the operation failed.
     */
    suspend fun createUser(
        firstName: String,
        lastName: String,
    ): Result<User> {
        logD(TAG, "createUser")
        val result = userDatastore.createUser(
            firstName,
            lastName,
        )
        if (result.isSuccess && settingsHolder.getBoolean(FlyerBoardSettingKey.LogAccountCreated) == true) {
            logD(TAG, "User created successfully: ${result.getOrThrow().id.userId}")
        }
        return result
    }

    companion object {
        private const val TAG = "UserService"
    }
}
