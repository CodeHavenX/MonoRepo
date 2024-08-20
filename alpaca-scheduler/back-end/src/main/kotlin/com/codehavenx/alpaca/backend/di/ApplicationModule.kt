package com.codehavenx.alpaca.backend.di

import com.codehavenx.alpaca.backend.config.createJson
import com.codehavenx.alpaca.backend.core.controller.AvailabilityController
import com.codehavenx.alpaca.backend.core.controller.UserController
import com.codehavenx.alpaca.backend.core.service.CalendarService
import com.codehavenx.alpaca.backend.core.service.ConfigurationService
import com.codehavenx.alpaca.backend.core.service.ReservationService
import com.codehavenx.alpaca.backend.core.service.UserService
import com.codehavenx.alpaca.backend.core.storage.CalendarDatabase
import com.codehavenx.alpaca.backend.core.storage.ConfigurationDatabase
import com.codehavenx.alpaca.backend.core.storage.UserDatabase
import com.codehavenx.alpaca.backend.core.storage.supabase.SupabaseCalendarDatabase
import com.codehavenx.alpaca.backend.core.storage.supabase.SupabaseConfigurationDatabase
import com.codehavenx.alpaca.backend.core.storage.supabase.SupabaseUserDatabase
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
val ApplicationModule = module(createdAtStart = true) {

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
        createSupabaseClient(
            supabaseUrl = "",
            supabaseKey = ""
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
    singleOf(::AvailabilityController)

    // Services
    singleOf(::UserService)
    singleOf(::CalendarService)
    singleOf(::ConfigurationService)
    singleOf(::ReservationService)

    // Storage
    singleOf(::SupabaseUserDatabase) {
        bind<UserDatabase>()
    }
    singleOf(::SupabaseCalendarDatabase) {
        bind<CalendarDatabase>()
    }
    singleOf(::SupabaseConfigurationDatabase) {
        bind<ConfigurationDatabase>()
    }
}
