package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.templatereplaceme.server.service.ComponentReplacemeService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Registers all service-layer singletons with Koin.
 *
 * Add new services here using [singleOf].
 */
internal val ServicesModule =
    module {
        singleOf(::ComponentReplacemeService)
    }
