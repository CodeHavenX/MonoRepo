package com.cramsan.edifikana.client.lib.service.impl

import io.github.jan.supabase.auth.Auth
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpHeaders

/**
 * A plugin that attaches the current Supabase access token to each request as a standard
 * `Authorization: Bearer <token>` credential. The token is read fresh on every request so that
 * Supabase remains the single authority over the token lifecycle (issuance and refresh).
 */
@Suppress("FunctionName")
fun AuthRequestPlugin(auth: Auth) =
    createClientPlugin("AuthRequestPlugin") {
        onRequest { request, _ ->
            auth.currentAccessTokenOrNull()?.let {
                request.headers.append(HttpHeaders.Authorization, "Bearer $it")
            }
        }
    }
