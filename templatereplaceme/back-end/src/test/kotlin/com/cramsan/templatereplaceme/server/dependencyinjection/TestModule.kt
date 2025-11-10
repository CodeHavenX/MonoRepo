package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.framework.core.ktor.Controller
import com.cramsan.templatereplaceme.server.controller.UserController
import com.cramsan.templatereplaceme.server.service.UserService
import io.mockk.mockk
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Produce a Ktor module for testing.
 */
val TestControllerModule = module {
    singleOf(::UserController) {
        bind<Controller>()
    }
}

val TestServiceModule = module {
    single<UserService> { mockk() }
}
