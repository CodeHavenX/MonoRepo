package com.cramsan.flyerboard.server.dependencyinjection

import com.cramsan.flyerboard.server.datastore.UserDatastore
import com.cramsan.flyerboard.server.datastore.impl.UserDatastoreImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val DatastoreModule = module {
    singleOf(::UserDatastoreImpl) {
        bind<UserDatastore>()
    }
}
