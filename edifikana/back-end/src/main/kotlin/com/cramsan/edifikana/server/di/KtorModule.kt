package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.server.core.controller.EventLogController
import com.cramsan.edifikana.server.core.controller.HealthCheckController
import com.cramsan.edifikana.server.core.controller.PropertyController
import com.cramsan.edifikana.server.core.controller.StaffController
import com.cramsan.edifikana.server.core.controller.TimeCardController
import com.cramsan.edifikana.server.core.controller.UserController
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize and bind the ktor components.
 */
val KtorModule = module {
    // Controllers
    singleOf(::UserController)
    singleOf(::EventLogController)
    singleOf(::HealthCheckController)
    singleOf(::PropertyController)
    singleOf(::StaffController)
    singleOf(::TimeCardController)
}
