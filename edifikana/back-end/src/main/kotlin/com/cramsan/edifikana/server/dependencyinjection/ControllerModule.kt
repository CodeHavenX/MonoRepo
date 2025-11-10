package com.cramsan.edifikana.server.dependencyinjection

import com.cramsan.edifikana.server.controller.EmployeeController
import com.cramsan.edifikana.server.controller.EventLogController
import com.cramsan.edifikana.server.controller.HealthCheckController
import com.cramsan.edifikana.server.controller.OrganizationController
import com.cramsan.edifikana.server.controller.PropertyController
import com.cramsan.edifikana.server.controller.StorageController
import com.cramsan.edifikana.server.controller.TimeCardController
import com.cramsan.edifikana.server.controller.UserController
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ControllerModule = module {
    singleOf(::UserController)
    singleOf(::EventLogController)
    singleOf(::HealthCheckController)
    singleOf(::PropertyController)
    singleOf(::EmployeeController)
    singleOf(::TimeCardController)
    singleOf(::StorageController)
    singleOf(::OrganizationController)
}
