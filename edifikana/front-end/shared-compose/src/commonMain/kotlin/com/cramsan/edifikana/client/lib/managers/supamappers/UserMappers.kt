package com.cramsan.edifikana.client.lib.managers.supamappers

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.supa.SupabaseModel
import com.cramsan.edifikana.lib.supa.User

@SupabaseModel
fun User.toDomainModel(): UserModel {
    return UserModel(
        id = id ?: TODO("User id cannot be null"),
        email = id ?: TODO("User email cannot be null"),
    )
}
