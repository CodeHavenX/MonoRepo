package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.ui.di.Coil3Provider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.coil.Coil3Integration
import io.github.jan.supabase.coil.coil3
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import org.koin.core.qualifier.named
import org.koin.dsl.module

@OptIn(SupabaseExperimental::class)
internal val SupabaseModule = module {
    single {
        val supabaseUrl = get<String>(named(EDIFIKANA_SUPABASE_URL))
        val supabaseKey = get<String>(named(EDIFIKANA_SUPABASE_KEY))

        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey,
        ) {
            install(Storage)
            install(Auth) {
                sessionManager = SettingsSessionManager(key = "$supabaseUrl-client")
            }
            install(ComposeAuth) {
                googleNativeLogin(
                    serverClientId = "225276838088-9pqht0mn9panaukdn8tpig8v6q1qfds0.apps.googleusercontent.com"
                )
                appleNativeLogin()
            }
            install(Coil3Integration)
        }
    }

    single<Coil3Provider> {
        Coil3Provider(get<Coil3Integration>())
    }

    single<Auth> {
        get<SupabaseClient>().auth
    }

    single<ComposeAuth> {
        get<SupabaseClient>().composeAuth
    }

    single<Storage> {
        get<SupabaseClient>().storage
    }

    single<Coil3Integration> {
        get<SupabaseClient>().coil3
    }
}
