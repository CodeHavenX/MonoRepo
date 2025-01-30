package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.lib.serialization.HEADER_TOKEN_AUTH
import io.github.jan.supabase.auth.Auth
import io.ktor.client.plugins.api.createClientPlugin

/**
 * A plugin that adds the current access token to the request headers.
 */
@Suppress("FunctionNaming")
fun AuthRequestPlugin(auth: Auth) = createClientPlugin("AuthRequestPlugin") {
    onRequest { request, _ ->
        auth.currentAccessTokenOrNull()?.let {
            request.headers.append(HEADER_TOKEN_AUTH, it)
        }
    }
}
