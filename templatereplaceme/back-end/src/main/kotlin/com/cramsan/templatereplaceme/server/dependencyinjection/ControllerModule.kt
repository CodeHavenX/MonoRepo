package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.templatereplaceme.server.controller.UserController
import com.cramsan.templatereplaceme.server.controller.authentication.ContextRetrieverImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize and bind the ktor components.
 */
val ControllerModule = module {
    singleOf(::UserController) {
        bind<Controller>()
    }
}
