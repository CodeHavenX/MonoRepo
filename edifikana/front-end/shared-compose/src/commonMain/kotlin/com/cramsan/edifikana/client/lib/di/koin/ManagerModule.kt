package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.managers.AttachmentManager
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.EmployeeManager
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.edifikana.client.lib.managers.remoteconfig.RemoteConfig
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.EmployeeService
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.client.lib.service.PropertyConfigService
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.service.dummy.DummyAuthService
import com.cramsan.edifikana.client.lib.service.dummy.DummyEmployeeService
import com.cramsan.edifikana.client.lib.service.dummy.DummyEventLogService
import com.cramsan.edifikana.client.lib.service.dummy.DummyPropertyConfigService
import com.cramsan.edifikana.client.lib.service.dummy.DummyStorageService
import com.cramsan.edifikana.client.lib.service.dummy.DummyTimeCardService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ManagerModule = module {
    single { WorkContext(get(), get(), get(), get()) }
    singleOf(::EventLogManager)
    singleOf(::AttachmentManager)
    singleOf(::TimeCardManager)
    singleOf(::EmployeeManager)
    singleOf(::AuthManager)
    single { get<RemoteConfig>().caching }
    single { get<RemoteConfig>().image }
    single { get<RemoteConfig>().behavior }
    single { get<RemoteConfig>().features }

    single {
        createSupabaseClient(
            supabaseUrl = "",
            supabaseKey = ""
        ) {
            install(Postgrest)
            install(Storage)
            install(Auth) {
            }
            install(ComposeAuth) {
                googleNativeLogin(
                    serverClientId = "225276838088-9pqht0mn9panaukdn8tpig8v6q1qfds0.apps.googleusercontent.com"
                )
                appleNativeLogin()
            }
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
        get<SupabaseClient>().postgrest
    }

    // Services
    singleOf(::DummyAuthService) {
        bind<AuthService>()
    }
    singleOf(::DummyEventLogService) {
        bind<EventLogService>()
    }
    singleOf(::DummyPropertyConfigService) {
        bind<PropertyConfigService>()
    }
    singleOf(::DummyStorageService) {
        bind<StorageService>()
    }
    singleOf(::DummyTimeCardService) {
        bind<TimeCardService>()
    }
    singleOf(::DummyEmployeeService) {
        bind<EmployeeService>()
    }
}
