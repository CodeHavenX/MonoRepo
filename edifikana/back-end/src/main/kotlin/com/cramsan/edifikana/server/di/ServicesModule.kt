package com.cramsan.edifikana.server.di

import com.cramsan.edifikana.server.core.service.EventLogService
import com.cramsan.edifikana.server.core.service.PropertyService
import com.cramsan.edifikana.server.core.service.StaffService
import com.cramsan.edifikana.server.core.service.StorageService
import com.cramsan.edifikana.server.core.service.TimeCardService
import com.cramsan.edifikana.server.core.service.UserService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize all the services level components.
 */
val ServicesModule = module {
    singleOf(::UserService)
    singleOf(::EventLogService)
    singleOf(::PropertyService)
    singleOf(::StaffService)
    singleOf(::TimeCardService)
    singleOf(::StorageService)
}
