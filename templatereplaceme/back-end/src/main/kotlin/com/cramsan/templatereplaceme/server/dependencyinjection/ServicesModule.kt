package com.cramsan.templatereplaceme.server.dependencyinjection

import com.cramsan.templatereplaceme.server.service.UserService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize all the services level components.
 */
val ServicesModule = module {
    singleOf(::UserService)
}
