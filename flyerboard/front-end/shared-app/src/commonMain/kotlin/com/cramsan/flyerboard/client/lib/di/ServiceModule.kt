package com.cramsan.flyerboard.client.lib.di

import com.cramsan.architecture.client.settings.SettingsHolder
import com.cramsan.flyerboard.client.lib.service.AuthService
import com.cramsan.flyerboard.client.lib.service.UserService
import com.cramsan.flyerboard.client.lib.service.impl.AuthServiceImpl
import com.cramsan.flyerboard.client.lib.service.impl.UserServiceImpl
import com.cramsan.flyerboard.client.lib.settings.FlyerBoardSettingKey
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ServiceModule = module {

    single {
        val settingsHolder: SettingsHolder = get()
        val supabaseUrl = settingsHolder.getString(FlyerBoardSettingKey.SupabaseUrl).orEmpty().trim()
        val supabaseKey = settingsHolder.getString(FlyerBoardSettingKey.SupabaseKey).orEmpty().trim()

        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey,
        ) {
            install(Auth) {
                sessionManager = SettingsSessionManager(key = "$supabaseUrl-flyerboard")
            }
        }
    }

    single<Auth> {
        get<SupabaseClient>().auth
    }

    singleOf(::AuthServiceImpl) {
        bind<AuthService>()
    }

    singleOf(::UserServiceImpl) {
        bind<UserService>()
    }
}
