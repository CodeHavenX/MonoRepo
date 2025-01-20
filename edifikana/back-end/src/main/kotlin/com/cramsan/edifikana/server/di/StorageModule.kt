package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.server.core.repository.EventLogDatabase
import com.cramsan.edifikana.server.core.repository.PropertyDatabase
import com.cramsan.edifikana.server.core.repository.StaffDatabase
import com.cramsan.edifikana.server.core.repository.TimeCardDatabase
import com.cramsan.edifikana.server.core.repository.UserDatabase
import com.cramsan.edifikana.server.core.repository.dummy.DummyEventLogDatabase
import com.cramsan.edifikana.server.core.repository.dummy.DummyPropertyDatabase
import com.cramsan.edifikana.server.core.repository.dummy.DummyStaffDatabase
import com.cramsan.edifikana.server.core.repository.dummy.DummyTimeCardDatabase
import com.cramsan.edifikana.server.core.repository.dummy.DummyUserDatabase
import com.cramsan.edifikana.server.core.repository.supabase.SupabaseEventLogDatabase
import com.cramsan.edifikana.server.core.repository.supabase.SupabasePropertyDatabase
import com.cramsan.edifikana.server.core.repository.supabase.SupabaseStaffDatabase
import com.cramsan.edifikana.server.core.repository.supabase.SupabaseTimeCardDatabase
import com.cramsan.edifikana.server.core.repository.supabase.SupabaseUserDatabase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val SupabaseStorageModule = module {
    // Storage
    singleOf(::SupabaseUserDatabase) {
        bind<UserDatabase>()
    }
    singleOf(::SupabaseStaffDatabase) {
        bind<StaffDatabase>()
    }
    singleOf(::SupabasePropertyDatabase) {
        bind<PropertyDatabase>()
    }
    singleOf(::SupabaseTimeCardDatabase) {
        bind<TimeCardDatabase>()
    }
    singleOf(::SupabaseEventLogDatabase) {
        bind<EventLogDatabase>()
    }
}

val DummyStorageModule = module {
    // Storage
    singleOf(::DummyUserDatabase) {
        bind<UserDatabase>()
    }
    singleOf(::DummyStaffDatabase) {
        bind<StaffDatabase>()
    }
    singleOf(::DummyPropertyDatabase) {
        bind<PropertyDatabase>()
    }
    singleOf(::DummyTimeCardDatabase) {
        bind<TimeCardDatabase>()
    }
    singleOf(::DummyEventLogDatabase) {
        bind<EventLogDatabase>()
    }
}
