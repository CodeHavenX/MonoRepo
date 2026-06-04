package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.templatereplaceme.client.lib.service.ComponentReplacemeService
import com.cramsan.templatereplaceme.client.lib.service.impl.ComponentReplacemeServiceImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Registers all front-end service implementations with Koin.
 *
 * Add new service bindings here using [singleOf] bound to the interface type.
 */
internal val ServiceModule =
    module {
        singleOf(::ComponentReplacemeServiceImpl) {
            bind<ComponentReplacemeService>()
        }
    }
