package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.framework.core.ktor.Controller
import com.cramsan.templatereplaceme.server.controller.UserController
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize and bind the ktor components.
 */
internal val ControllerModule = module {
    singleOf(::UserController) {
        bind<Controller>()
    }
}
