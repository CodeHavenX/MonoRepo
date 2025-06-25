package com.cramsan.edifikana.server.di

import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize the integ test pplication level components.
 */
val IntegTestApplicationModule = module {
    single<String>(named(STAGE_KEY)) {
        "integ"
    }
}

