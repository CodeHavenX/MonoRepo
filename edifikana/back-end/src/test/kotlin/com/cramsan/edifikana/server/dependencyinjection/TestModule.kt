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
import com.cramsan.edifikana.server.service.EmployeeService
import com.cramsan.edifikana.server.service.EventLogService
import com.cramsan.edifikana.server.service.NotificationService
import com.cramsan.edifikana.server.service.OrganizationService
import com.cramsan.edifikana.server.service.PropertyService
import com.cramsan.edifikana.server.service.StorageService
import com.cramsan.edifikana.server.service.TimeCardService
import com.cramsan.edifikana.server.service.UserService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * A test module for the controllers.
 */
internal val TestControllerModule = module {
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

/**
 * A test module for the services.
 */
internal val TestServiceModule = module {
    single<UserService> { mockk() }
    single<EventLogService> { mockk() }
    single<PropertyService> { mockk() }
    single<EmployeeService> { mockk() }
    single<TimeCardService> { mockk() }
    single<StorageService> { mockk() }
    single<OrganizationService> { mockk() }
    single<RBACService> { mockk() }
    single<NotificationService> { mockk() }
}

internal fun testApplicationModule(json: Json) = module {
    single<Json> { json }

    single<ContextRetriever<*>> { mockk() }
}
