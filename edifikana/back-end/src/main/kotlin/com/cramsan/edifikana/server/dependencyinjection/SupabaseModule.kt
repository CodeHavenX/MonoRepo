package com.cramsan.edifikana.server.dependencyinjection

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
import com.cramsan.edifikana.server.dependencyinjection.settings.Overrides
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

private const val TAG = "SupabaseModule"
