package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.templatereplaceme.server.datastore.PingPongDatastore
import com.cramsan.templatereplaceme.server.datastore.impl.ExamplePingPongDatastore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val DatastoreModule =
    module {
        singleOf(::ExamplePingPongDatastore) {
            bind<PingPongDatastore>()
        }
    }
