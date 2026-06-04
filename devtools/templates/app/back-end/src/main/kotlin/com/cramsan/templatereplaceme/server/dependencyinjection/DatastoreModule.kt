package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.templatereplaceme.server.datastore.ComponentReplacemeDatastore
import com.cramsan.templatereplaceme.server.datastore.impl.ExampleComponentReplacemeDatastore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Registers all datastore implementations with Koin.
 *
 * Replace [ExampleComponentReplacemeDatastore] with your real persistence implementation.
 */
internal val DatastoreModule =
    module {
        singleOf(::ExampleComponentReplacemeDatastore) {
            bind<ComponentReplacemeDatastore>()
        }
    }
