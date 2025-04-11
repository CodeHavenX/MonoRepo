package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.service.impl.AuthRequestPlugin
import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.edifikana.lib.serialization.createJson
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val KtorModule = module {

    single<HttpClient> {
        HttpClient {
            defaultRequest {
                url("http://0.0.0.0:9292")
            }
            install(ContentNegotiation) {
                json(createJson())
            }
            val disableSupabase = get<Boolean>(named(Overrides.KEY_DISABLE_SUPABASE))
            if (!disableSupabase) {
                install(AuthRequestPlugin(get()))
            }
        }
    }

}