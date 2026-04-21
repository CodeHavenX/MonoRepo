package com.cramsan.flyerboard.server.dependencyinjection

import com.cramsan.framework.core.ktor.Controller
import com.cramsan.flyerboard.server.controller.FlyerController
import com.cramsan.flyerboard.server.controller.HealthController
import com.cramsan.flyerboard.server.controller.ModerationController
import com.cramsan.flyerboard.server.controller.UserController
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
