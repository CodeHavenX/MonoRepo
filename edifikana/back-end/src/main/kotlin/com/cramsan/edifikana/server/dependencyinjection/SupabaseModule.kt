package com.cramsan.edifikana.server.dependencyinjection

import com.cramsan.edifikana.server.PropertyKey
import com.cramsan.edifikana.server.SettingsHolder
import com.cramsan.edifikana.server.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextRetriever
import com.cramsan.edifikana.server.datastore.EmployeeDatastore
import com.cramsan.edifikana.server.datastore.EventLogDatastore
import com.cramsan.edifikana.server.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.datastore.PropertyDatastore
import com.cramsan.edifikana.server.datastore.StorageDatastore
import com.cramsan.edifikana.server.datastore.TimeCardDatastore
import com.cramsan.edifikana.server.datastore.UserDatastore
import com.cramsan.edifikana.server.datastore.supabase.SupabaseEmployeeDatastore
import com.cramsan.edifikana.server.datastore.supabase.SupabaseEventLogDatastore
import com.cramsan.edifikana.server.datastore.supabase.SupabaseOrganizationDatastore
import com.cramsan.edifikana.server.datastore.supabase.SupabasePropertyDatastore
import com.cramsan.edifikana.server.datastore.supabase.SupabaseStorageDatastore
import com.cramsan.edifikana.server.datastore.supabase.SupabaseTimeCardDatastore
import com.cramsan.edifikana.server.datastore.supabase.SupabaseUserDatastore
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
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the supabase module.
 */
val SupabaseModule = module {

    single {
        val settingsHolder: SettingsHolder = get()
        val supabaseUrl: String = settingsHolder.getString(PropertyKey.SUPABASE_URL).orEmpty()
        val supabaseKey: String = settingsHolder.getString(PropertyKey.SUPABASE_KEY).orEmpty()

        if (supabaseUrl.isBlank()) {
            val supabaseUrlKeyNames = settingsHolder.getKeyNames(PropertyKey.SUPABASE_URL).joinToString()
            error("Value needs to be provided in one of the following settings: $supabaseUrlKeyNames")
        }

        if (supabaseKey.isBlank()) {
            val supabaseKeySettingKeyName = settingsHolder.getKeyNames(PropertyKey.SUPABASE_KEY).joinToString()
            error("Value needs to be provided in one of the following settings: $supabaseKeySettingKeyName")
        }

        val stageSegment: String = get(named(NamedDependency.STAGE_KEY))

        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey,
        ) {
            install(Postgrest)
            install(Storage)
            install(Auth) {
                sessionManager = SettingsSessionManager(key = "$supabaseUrl-server-$stageSegment")
            }
        }
    }

    // Supabase Components
    single {
        get<SupabaseClient>().auth
    }

    single {
        get<SupabaseClient>().auth.admin
    }

    single {
        get<SupabaseClient>().storage
    }

    single {
        get<SupabaseClient>().postgrest
    }

    // Supabase Datastores
    singleOf(::SupabaseUserDatastore) {
        bind<UserDatastore>()
    }
    singleOf(::SupabaseEmployeeDatastore) {
        bind<EmployeeDatastore>()
    }
    singleOf(::SupabasePropertyDatastore) {
        bind<PropertyDatastore>()
    }
    singleOf(::SupabaseTimeCardDatastore) {
        bind<TimeCardDatastore>()
    }
    singleOf(::SupabaseEventLogDatastore) {
        bind<EventLogDatastore>()
    }
    singleOf(::SupabaseStorageDatastore) {
        bind<StorageDatastore>()
    }
    singleOf(::SupabaseOrganizationDatastore) {
        bind<OrganizationDatastore>()
    }

    // Other Components
    singleOf(::SupabaseContextRetriever) {
        bind<ContextRetriever>()
    }
}
