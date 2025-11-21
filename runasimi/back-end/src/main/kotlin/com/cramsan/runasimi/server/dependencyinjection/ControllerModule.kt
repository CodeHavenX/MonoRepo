package com.cramsan.runasimi.server.dependencyinjection

import com.cramsan.framework.core.ktor.Controller
import com.cramsan.runasimi.server.controller.RunasimiController
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Initialize and bind controllers used by the runasimi backend.
 */
internal val ControllerModule = module {
    singleOf(::RunasimiController) {
        bind<Controller>()
    }
}
