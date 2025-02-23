package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.core.controller.EventLogController
import com.cramsan.edifikana.server.core.controller.HealthCheckController
import com.cramsan.edifikana.server.core.controller.PropertyController
import com.cramsan.edifikana.server.core.controller.StaffController
import com.cramsan.edifikana.server.core.controller.TimeCardController
import com.cramsan.edifikana.server.core.controller.UserController
import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.controller.auth.SupabaseContextRetriever
import com.cramsan.edifikana.server.core.service.EventLogService
import com.cramsan.edifikana.server.core.service.PropertyService
import com.cramsan.edifikana.server.core.service.StaffService
import com.cramsan.edifikana.server.core.service.TimeCardService
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.edifikana.server.core.service.password.PasswordGenerator
import com.cramsan.edifikana.server.core.service.password.SimplePasswordGenerator
import com.cramsan.edifikana.server.settings.Overrides
import com.cramsan.edifikana.server.util.readSimpleProperty
import com.cramsan.framework.assertlib.assertFalse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
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
import org.koin.core.qualifier.named
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

    singleOf(::SimplePasswordGenerator) {
        bind<PasswordGenerator>()
    }

    singleOf(::SupabaseContextRetriever) {
        bind<ContextRetriever>()
    }

    // Supabase
    single {
        val disableSupabase: Boolean = get(named(Overrides.KEY_SUPABASE_DISABLE))
        assertFalse(
            disableSupabase,
            TAG,
            "SupabaseClient was loaded while in debug mode. This may be due to incorrectly configured DI.",
        )

        val supabaseUrl = readSimpleProperty("ediifkana.supabase.url") ?: System.getenv("EDIFIKANA_SUPABASE_URL")
        val supabaseKey = readSimpleProperty("edifikana.supabase.key") ?: System.getenv("EDIFIKANA_SUPABASE_KEY")

        assertFalse(
            supabaseUrl.isNullOrBlank(),
            TAG,
            "EDIFIKANA_SUPABASE_URL or ediifkana.supabase.url cannot be blank"
        )
        assertFalse(
            supabaseKey.isNullOrBlank(),
            TAG,
            "EDIFIKANA_SUPABASE_KEY or edifikana.supabase.key cannot be blank"
        )

        createSupabaseClient(
            supabaseUrl = supabaseUrl.orEmpty(),
            supabaseKey = supabaseKey.orEmpty(),
        ) {
            install(Postgrest)
            install(Storage)
            install(Auth) {
                sessionManager = SettingsSessionManager(key = "$supabaseUrl-server")
            }
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
    singleOf(::EventLogController)
    singleOf(::HealthCheckController)
    singleOf(::PropertyController)
    singleOf(::StaffController)
    singleOf(::TimeCardController)

    // Services
    singleOf(::UserService)
    singleOf(::EventLogService)
    singleOf(::PropertyService)
    singleOf(::StaffService)
    singleOf(::TimeCardService)
}

private const val TAG = "ApplicationModule"
