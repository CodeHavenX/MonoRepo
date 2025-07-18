package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.server.core.controller.auth.ContextRetriever
import com.cramsan.edifikana.server.core.controller.auth.DummyContextRetriever
import com.cramsan.edifikana.server.core.datastore.EventLogDatastore
import com.cramsan.edifikana.server.core.datastore.PropertyDatastore
import com.cramsan.edifikana.server.core.datastore.StaffDatastore
import com.cramsan.edifikana.server.core.datastore.TimeCardDatastore
import com.cramsan.edifikana.server.core.datastore.UserDatastore
import com.cramsan.edifikana.server.core.datastore.dummy.DummyEventLogDatastore
import com.cramsan.edifikana.server.core.datastore.dummy.DummyPropertyDatastore
import com.cramsan.edifikana.server.core.datastore.dummy.DummyStaffDatastore
import com.cramsan.edifikana.server.core.datastore.dummy.DummyTimeCardDatastore
import com.cramsan.edifikana.server.core.datastore.dummy.DummyUserDatastore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val DummyStorageModule = module {
    // Storage
    singleOf(::DummyUserDatastore) {
        bind<UserDatastore>()
    }
    singleOf(::DummyStaffDatastore) {
        bind<StaffDatastore>()
    }
    singleOf(::DummyPropertyDatastore) {
        bind<PropertyDatastore>()
    }
    singleOf(::DummyTimeCardDatastore) {
        bind<TimeCardDatastore>()
    }
    singleOf(::DummyEventLogDatastore) {
        bind<EventLogDatastore>()
    }
    singleOf(::DummyContextRetriever) {
        bind<ContextRetriever>()
    }
}
