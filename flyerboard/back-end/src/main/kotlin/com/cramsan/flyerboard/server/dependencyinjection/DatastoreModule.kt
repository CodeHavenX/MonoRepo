package com.cramsan.flyerboard.server.dependencyinjection

import com.cramsan.architecture.server.settings.SettingsHolder
import com.cramsan.flyerboard.server.datastore.FileDatastore
import com.cramsan.flyerboard.server.datastore.FlyerDatastore
import com.cramsan.flyerboard.server.datastore.UserDatastore
import com.cramsan.flyerboard.server.datastore.UserProfileDatastore
import com.cramsan.flyerboard.server.datastore.impl.SupabaseFileDatastore
import com.cramsan.flyerboard.server.datastore.impl.SupabaseFlyerDatastore
import com.cramsan.flyerboard.server.datastore.impl.UserDatastoreImpl
import com.cramsan.flyerboard.server.datastore.impl.SupabaseUserProfileDatastore
import com.cramsan.flyerboard.server.settings.FlyerBoardSettingKey
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val DatastoreModule = module {

    single {
        val settingsHolder: SettingsHolder = get()
        val supabaseUrl = settingsHolder.getString(FlyerBoardSettingKey.SupabaseUrl).orEmpty()
        val supabaseKey = settingsHolder.getString(FlyerBoardSettingKey.SupabaseKey).orEmpty()

        if (supabaseUrl.isBlank()) {
            val keyNames = settingsHolder.getKeyNames(FlyerBoardSettingKey.SupabaseUrl).joinToString()
            error("Supabase URL must be provided in one of: $keyNames")
        }
        if (supabaseKey.isBlank()) {
            val keyNames = settingsHolder.getKeyNames(FlyerBoardSettingKey.SupabaseKey).joinToString()
            error("Supabase key must be provided in one of: $keyNames")
        }

        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey,
        ) {
            install(Postgrest)
            install(Storage)
            install(Auth) {
                sessionManager = SettingsSessionManager(key = "$supabaseUrl-flyerboard-server")
            }
        }
    }

    single<Auth> { get<SupabaseClient>().auth }
    single<Postgrest> { get<SupabaseClient>().postgrest }
    single<Storage> { get<SupabaseClient>().storage }

    singleOf(::UserDatastoreImpl) {
        bind<UserDatastore>()
    }

    singleOf(::SupabaseFlyerDatastore) {
        bind<FlyerDatastore>()
    }

    singleOf(::SupabaseFileDatastore) {
        bind<FileDatastore>()
    }

    singleOf(::SupabaseUserProfileDatastore) {
        bind<UserProfileDatastore>()
    }
}
