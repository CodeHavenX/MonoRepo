package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.templatereplaceme.server.datastore.ComponentReplaceMeDatastore
import com.cramsan.templatereplaceme.server.datastore.impl.ExampleComponentReplaceMeDatastore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Registers all datastore implementations with Koin.
 *
 * Replace [ExampleComponentReplaceMeDatastore] with your real persistence implementation.
 */
internal val DatastoreModule =
    module {
        singleOf(::ExampleComponentReplaceMeDatastore) {
            bind<ComponentReplaceMeDatastore>()
        }
    }
