package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.controller.auth.SupabaseContextRetriever
import com.cramsan.edifikana.server.core.datastore.EventLogDatastore
import com.cramsan.edifikana.server.core.datastore.PropertyDatastore
import com.cramsan.edifikana.server.core.datastore.StaffDatastore
import com.cramsan.edifikana.server.core.datastore.StorageDatastore
import com.cramsan.edifikana.server.core.datastore.TimeCardDatastore
import com.cramsan.edifikana.server.core.datastore.UserDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.SupabaseEventLogDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.SupabasePropertyDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.SupabaseStaffDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.SupabaseStorageDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.SupabaseTimeCardDatastore
import com.cramsan.edifikana.server.core.datastore.supabase.SupabaseUserDatastore
import com.cramsan.edifikana.server.settings.Overrides
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
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize all the supabase module.
 */
val SupabaseModule = module {

    single {
        val disableSupabase: Boolean = get(named(Overrides.KEY_SUPABASE_DISABLE))
        assertFalse(
            disableSupabase,
            TAG,
            "SupabaseClient was loaded while in debug mode. This may be due to incorrectly configured DI.",
        )

        val supabaseUrl: String = get(named(Overrides.KEY_SUPABASE_URL))
        val supabaseKey: String = get(named(Overrides.KEY_SUPABASE_KEY))

        assertFalse(
            supabaseUrl.isBlank(),
            TAG,
            "EDIFIKANA_SUPABASE_URL or edifikana.supabase.url cannot be blank"
        )
        assertFalse(
            supabaseKey.isBlank(),
            TAG,
            "EDIFIKANA_SUPABASE_KEY or edifikana.supabase.key cannot be blank"
        )

        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey,
        ) {
            install(Postgrest)
            install(Storage)
            install(Auth) {
                sessionManager = SettingsSessionManager(key = "$supabaseUrl-server-${getStageSegment()}")
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
    singleOf(::SupabaseStaffDatastore) {
        bind<StaffDatastore>()
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

    // Other Components
    singleOf(::SupabaseContextRetriever) {
        bind<ContextRetriever>()
    }
}

private const val TAG = "SupabaseModule"
