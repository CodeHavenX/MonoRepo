package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.settings.Overrides
import com.cramsan.framework.assertlib.assertFalse
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
        val disableSupabase = get<Boolean>(named(Overrides.KEY_DISABLE_SUPABASE))
        assertFalse(
            disableSupabase,
            TAG,
            "SupabaseClient was loaded while in debug mode. This may be due to incorrectly configured DI.",
        )

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

    single {
        get<SupabaseClient>().auth
    }

    single {
        get<SupabaseClient>().composeAuth
    }

    single {
        get<SupabaseClient>().storage
    }

    single {
        get<SupabaseClient>().coil3
    }
}

private const val TAG = "SupabaseModule"
