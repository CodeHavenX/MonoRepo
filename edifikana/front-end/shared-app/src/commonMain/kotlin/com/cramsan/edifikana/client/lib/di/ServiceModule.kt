package com.cramsan.edifikana.client.lib.di

import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.EmployeeService
import com.cramsan.edifikana.client.lib.service.EventLogService
import com.cramsan.edifikana.client.lib.service.NotificationService
import com.cramsan.edifikana.client.lib.service.OrganizationService
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.client.lib.service.TimeCardService
import com.cramsan.edifikana.client.lib.service.impl.AuthRequestPlugin
import com.cramsan.edifikana.client.lib.service.impl.AuthServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.EmployeeServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.EventLogServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.NotificationServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.OrganizationServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.PropertyServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.StorageServiceImpl
import com.cramsan.edifikana.client.lib.service.impl.TimeCardServiceImpl
import com.cramsan.edifikana.client.lib.settings.EdifikanaSettingKey
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
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@OptIn(SupabaseExperimental::class)
internal val ServiceModule = module {

    single {
        val settingsHolder: SettingsHolder = get()
        val supabaseUrl = settingsHolder.getString(EdifikanaSettingKey.SupabaseOverrideUrl).orEmpty().trim()
        val supabaseKey = settingsHolder.getString(EdifikanaSettingKey.SupabaseOverrideKey).orEmpty().trim()

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

    singleOf(::AuthServiceImpl) {
        bind<AuthService>()
    }
    singleOf(::EventLogServiceImpl) {
        bind<EventLogService>()
    }
    singleOf(::PropertyServiceImpl) {
        bind<PropertyService>()
    }
    singleOf(::StorageServiceImpl) {
        bind<StorageService>()
    }
    singleOf(::TimeCardServiceImpl) {
        bind<TimeCardService>()
    }
    singleOf(::EmployeeServiceImpl) {
        bind<EmployeeService>()
    }
    singleOf(::OrganizationServiceImpl) {
        bind<OrganizationService>()
    }
    singleOf(::NotificationServiceImpl) {
        bind<NotificationService>()
    }

    single {
        AuthRequestPlugin(get())
    }
}
