package com.cramsan.edifikana.server.di

import com.cramsan.framework.configuration.Configuration
import com.cramsan.framework.configuration.NoopConfiguration
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Class to initialize the integ test pplication level components.
 */
val IntegTestApplicationModule = module {
    single<Configuration> {
        NoopConfiguration()
    }

    single<String>(named(STAGE_KEY)) {
        "integ"
    }
}

