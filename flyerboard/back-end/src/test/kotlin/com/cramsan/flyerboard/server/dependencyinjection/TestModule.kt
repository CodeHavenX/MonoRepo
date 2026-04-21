package com.cramsan.flyerboard.server.dependencyinjection

import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.flyerboard.server.controller.FlyerController
import com.cramsan.flyerboard.server.controller.HealthController
import com.cramsan.flyerboard.server.controller.ModerationController
import com.cramsan.flyerboard.server.controller.UserController
import com.cramsan.flyerboard.server.service.FlyerService
import com.cramsan.flyerboard.server.service.ModerationService
import com.cramsan.flyerboard.server.service.UserService
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal fun testApplicationModule(json: Json) = module {
    single<Json> { json }

    single<ContextRetriever<*>> { mockk() }

    // Add additional test bindings if necessary
}

/**
 * Produce a Ktor module for testing.
 */
internal val TestControllerModule = module {
    singleOf(::UserController) {
        bind<Controller>()
    }
}

internal val TestServiceModule = module {
    single<UserService> { mockk() }
}

/**
 * Ktor module for testing FlyerController, ModerationController, and HealthController.
 */
internal val TestFlyerControllerModule = module {
    singleOf(::FlyerController) {
        bind<Controller>()
    }
    singleOf(::ModerationController) {
        bind<Controller>()
    }
    singleOf(::HealthController) {
        bind<Controller>()
    }
}

/**
 * Service mocks for FlyerController and ModerationController tests.
 */
internal val TestFlyerServiceModule = module {
    single<FlyerService> { mockk() }
    single<ModerationService> { mockk() }
}
