package com.cramsan.flyerboard.server.dependencyinjection

import com.cramsan.flyerboard.server.service.UserService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize all the services level components.
 */
internal val ServicesModule = module {
    singleOf(::UserService)
}
