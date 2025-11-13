package com.cramsan.edifikana.server.dependencyinjection

import com.cramsan.edifikana.server.service.EmployeeService
import com.cramsan.edifikana.server.service.EventLogService
import com.cramsan.edifikana.server.service.OrganizationService
import com.cramsan.edifikana.server.service.PropertyService
import com.cramsan.edifikana.server.service.StorageService
import com.cramsan.edifikana.server.service.TimeCardService
import com.cramsan.edifikana.server.service.UserService
import com.cramsan.edifikana.server.service.authorization.RBACService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize all the services level components.
 */
internal val ServicesModule = module {
    singleOf(::UserService)
    singleOf(::EventLogService)
    singleOf(::PropertyService)
    singleOf(::EmployeeService)
    singleOf(::TimeCardService)
    singleOf(::StorageService)
    singleOf(::OrganizationService)
    singleOf(::RBACService)
}
