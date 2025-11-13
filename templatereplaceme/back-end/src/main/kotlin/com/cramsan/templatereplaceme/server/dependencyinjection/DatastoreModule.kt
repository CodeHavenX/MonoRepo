package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.templatereplaceme.server.datastore.UserDatastore
import com.cramsan.templatereplaceme.server.datastore.impl.UserDatastoreImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val DatastoreModule = module {
    singleOf(::UserDatastoreImpl) {
        bind<UserDatastore>()
    }
}
