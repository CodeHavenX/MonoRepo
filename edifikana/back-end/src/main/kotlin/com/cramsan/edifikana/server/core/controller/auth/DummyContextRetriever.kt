package com.cramsan.edifikana.server.core.controller.auth

import com.cramsan.edifikana.lib.model.UserId
import io.github.jan.supabase.auth.user.UserInfo
import io.ktor.server.application.ApplicationCall

/**
 * A context retriever that always returns a dummy context.
 */
class DummyContextRetriever : ContextRetriever {
    override suspend fun getContext(applicationCall: ApplicationCall): ClientContext {
        return ClientContext.AuthenticatedClientContext(
            userInfo = UserInfo(
                id = "c58e6c54-8f47-4dbe-a0a8-655fc9f8c104",
                email = "cramsan@test.com",
                aud = ""
            ),
            userId = UserId("c58e6c54-8f47-4dbe-a0a8-655fc9f8c104"),
        )
    }
}
