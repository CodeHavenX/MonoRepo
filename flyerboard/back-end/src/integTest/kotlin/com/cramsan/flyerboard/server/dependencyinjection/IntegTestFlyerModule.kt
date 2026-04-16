package com.cramsan.flyerboard.server.dependencyinjection

import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.flyerboard.server.controller.FlyerController
import com.cramsan.flyerboard.server.controller.HealthController
import com.cramsan.flyerboard.server.controller.ModerationController
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.service.FlyerService
import com.cramsan.flyerboard.server.service.ModerationService
import com.cramsan.flyerboard.lib.serialization.createJson
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Application-level DI module for integration tests.
 *
 * Provides mocked [ContextRetriever] and [Json] for the Ktor test server.
 */
fun integTestFlyerApplicationModule() = module {
    single<Json> { createJson() }
    single<ContextRetriever<FlyerBoardContextPayload>> { mockk() }
}

/**
 * Controller DI module for integration tests covering flyer, moderation, and health endpoints.
 */
val IntegTestFlyerControllerModule = module {
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
 * Service mocks for flyer integration tests.
 */
val IntegTestFlyerServiceModule = module {
    single<FlyerService> { mockk() }
    single<ModerationService> { mockk() }
}
