package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.User

@FireStoreModel
fun User.toDomainModel(): UserModel {
    return UserModel(
        id = id ?: TODO("User id cannot be null"),
        email = id ?: TODO("User email cannot be null"),
    )
}
