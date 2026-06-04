package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.framework.core.ktor.Controller
import com.cramsan.templatereplaceme.server.controller.ComponentReplaceMeController
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Registers all [Controller] instances with Koin.
 *
 * Add new controllers here using [singleOf] bound to [Controller].
 */
internal val ControllerModule =
    module {
        singleOf(::ComponentReplaceMeController) {
            bind<Controller>()
        }
    }
