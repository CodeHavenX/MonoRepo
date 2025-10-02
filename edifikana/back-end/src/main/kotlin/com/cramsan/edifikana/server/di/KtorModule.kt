package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.server.core.controller.EventLogController
import com.cramsan.edifikana.server.core.controller.HealthCheckController
import com.cramsan.edifikana.server.core.controller.OrganizationController
import com.cramsan.edifikana.server.core.controller.PropertyController
import com.cramsan.edifikana.server.core.controller.EmployeeController
import com.cramsan.edifikana.server.core.controller.StorageController
import com.cramsan.edifikana.server.core.controller.TimeCardController
import com.cramsan.edifikana.server.core.controller.UserController
import org.koin.core.module.Module
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
    singleOf(::EmployeeController)
    singleOf(::TimeCardController)
    singleOf(::StorageController)
    singleOf(::OrganizationController)

    registerControllers()
}

/**
 * Registers all controllers in a single list.
 */
fun Module.registerControllers() {
    // When adding a new controller, remember to add it to the list below
    single {
        val userController: UserController by inject()
        val eventLogController: EventLogController by inject()
        val propertyController: PropertyController by inject()
        val employeeController: EmployeeController by inject()
        val timeCardController: TimeCardController by inject()
        val healthCheckController: HealthCheckController by inject()
        val storageController: StorageController by inject()
        val organizationController: OrganizationController by inject()

        listOf(
            userController,
            eventLogController,
            propertyController,
            employeeController,
            timeCardController,
            healthCheckController,
            storageController,
            organizationController,
        )
    }
}
