package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.managers.AttachmentManager
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.edifikana.client.lib.managers.StaffManager
import com.cramsan.edifikana.client.lib.managers.TimeCardManager
import com.cramsan.edifikana.client.lib.managers.WorkContext
import com.cramsan.edifikana.client.lib.managers.remoteconfig.RemoteConfig
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.service.dummy.DummyAuthService
import com.cramsan.edifikana.client.lib.service.dummy.DummyStorageService
import com.cramsan.edifikana.client.lib.service.impl.EventLogServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.PropertyServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.StaffServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.TimeCardServiceImpl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ManagerModule = module {
    single { WorkContext(get(), get(), get(), get()) }

    singleOf(::EventLogManager)
    singleOf(::AttachmentManager)
    singleOf(::TimeCardManager)
    singleOf(::StaffManager)
    singleOf(::AuthManager)
    singleOf(::PropertyManager)

    single { get<RemoteConfig>().caching }
    single { get<RemoteConfig>().image }
    single { get<RemoteConfig>().behavior }
    single { get<RemoteConfig>().features }

    single {
        val supabaseUrl = get<String>(named(EDIFIKANA_SUPABASE_URL))
        val supabaseKey = get<String>(named(EDIFIKANA_SUPABASE_KEY))

        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey,
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
    singleOf(::EventLogServiceImpl) {
        bind<EventLogService>()
    }
    singleOf(::PropertyServiceImpl) {
        bind<PropertyService>()
    }
    singleOf(::DummyStorageService) {
        bind<StorageService>()
    }
    singleOf(::TimeCardServiceImpl) {
    singleOf(::SupabaseTimeCardService) {
        bind<TimeCardService>()
    }
    singleOf(::StaffServiceImpl) {
    singleOf(::SupabaseStaffService) {
        bind<StaffService>()
    }
}

internal const val EDIFIKANA_SUPABASE_URL = "EDIFIKANA_SUPABASE_URL"
internal const val EDIFIKANA_SUPABASE_KEY = "EDIFIKANA_SUPABASE_KEY"
