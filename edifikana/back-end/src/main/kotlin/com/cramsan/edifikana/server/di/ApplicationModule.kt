package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.core.controller.UserController
import com.cramsan.edifikana.server.core.repository.UserDatabase
import com.cramsan.edifikana.server.core.repository.supabase.SupabaseUserDatabase
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.framework.assertlib.assertFalse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize all the application level components.
 */
val ApplicationModule = module {

    single<CoroutineScope> {
        GlobalScope
    }

    single<Json> {
        createJson()
    }

    single<Clock> {
        Clock.System
    }

    // Supabase
    single {
        val supabaseUrl = System.getenv("edifikana_SUPABASE_URL")
        val supabaseKey = System.getenv("edifikana_SUPABASE_KEY")

        assertFalse(supabaseUrl.isNullOrBlank(), TAG, "edifikana_SUPABASE_URL cannot be blank")
        assertFalse(supabaseKey.isNullOrBlank(), TAG, "edifikana_SUPABASE_KEY cannot be blank")

        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey,
        ) {
            install(Postgrest)
            install(Storage)
            install(Auth)
        }
    }

    single {
        get<SupabaseClient>().auth
    }

    single {
        get<SupabaseClient>().storage
    }

    single {
        get<SupabaseClient>().postgrest
    }

    // Controllers
    singleOf(::UserController)

    // Services
    singleOf(::UserService)

    // Storage
    singleOf(::SupabaseUserDatabase) {
        bind<UserDatabase>()
    }
}

private const val TAG = "ApplicationModule"
