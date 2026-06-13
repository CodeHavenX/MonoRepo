package com.cramsan.flyerboard.server.dependencyinjection

import com.cramsan.framework.core.ktor.Controller
import com.cramsan.flyerboard.server.controller.UserController
import com.cramsan.flyerboard.server.service.UserService
import io.mockk.mockk
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Controller DI module for integration tests covering the user endpoint.
 */
val IntegTestUserControllerModule = module {
    singleOf(::UserController) {
        bind<Controller>()
    }
}

/**
 * Service mocks for user integration tests.
 */
val IntegTestUserServiceModule = module {
    single<UserService> { mockk() }
}
