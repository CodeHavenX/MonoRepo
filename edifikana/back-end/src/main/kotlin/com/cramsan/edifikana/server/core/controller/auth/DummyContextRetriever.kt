package com.cramsan.edifikana.server.core.controller.auth

import com.cramsan.edifikana.server.core.datastore.dummy.USER_1
import com.cramsan.framework.logging.logI
import io.github.jan.supabase.auth.user.UserInfo
import io.ktor.server.application.ApplicationCall
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * A [ContextRetriever] that retrieves a dummy client context.
 */
class DummyContextRetriever : ContextRetriever {

    @OptIn(ExperimentalTime::class)
    override suspend fun getContext(applicationCall: ApplicationCall): ClientContext {
        logI(TAG, "DummyContextRetriever called")
        return ClientContext.AuthenticatedClientContext(
            userInfo = UserInfo(
                aud = "aud",
                id = "id",
                createdAt = Clock.System.now()
            ),
            userId = USER_1.id,
        )
    }

    companion object {
        private const val TAG = "DummyContextRetriever"
    }
}
