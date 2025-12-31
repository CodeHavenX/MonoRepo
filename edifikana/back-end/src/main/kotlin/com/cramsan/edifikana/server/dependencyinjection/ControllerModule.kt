package com.cramsan.edifikana.server.dependencyinjection

import com.cramsan.edifikana.server.controller.EmployeeController
import com.cramsan.edifikana.server.controller.EventLogController
import com.cramsan.edifikana.server.controller.HealthCheckController
import com.cramsan.edifikana.server.controller.NotificationController
import com.cramsan.edifikana.server.controller.OrganizationController
import com.cramsan.edifikana.server.controller.PropertyController
import com.cramsan.edifikana.server.controller.StorageController
import com.cramsan.edifikana.server.controller.TimeCardController
import com.cramsan.edifikana.server.controller.UserController
import com.cramsan.framework.core.ktor.Controller
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Koin module for initializing all controller components in the Edifikana application.
 * Controllers are responsible for handling HTTP requests and routing them to appropriate services.
 */
internal val ControllerModule = module {
    singleOf(::UserController) { bind<Controller>() }
    singleOf(::EventLogController) { bind<Controller>() }
    singleOf(::HealthCheckController) { bind<Controller>() }
    singleOf(::PropertyController) { bind<Controller>() }
    singleOf(::EmployeeController) { bind<Controller>() }
    singleOf(::TimeCardController) { bind<Controller>() }
    singleOf(::StorageController) { bind<Controller>() }
    singleOf(::OrganizationController) { bind<Controller>() }
    singleOf(::NotificationController) { bind<Controller>() }
}
