package com.cramsan.edifikana.server.core.controller.auth

import com.cramsan.edifikana.lib.model.UserId
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.user.UserInfo

data class ClientContext(
    val userInfo: UserInfo,
    val userId: UserId,
)

suspend fun createClientContext(
    auth: Auth,
    userToken: String,
): ClientContext {
    /*
    val user = auth.retrieveUser(userToken)

    return ClientContext(
        userInfo = user,
        userId = UserId(user.id),
    )
     */
    return ClientContext(
        userInfo = UserInfo(
            id = "c58e6c54-8f47-4dbe-a0a8-655fc9f8c104",
            email = "",
            aud = ""
        ),
        userId = UserId("c58e6c54-8f47-4dbe-a0a8-655fc9f8c104"),
    )
}