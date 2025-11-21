package com.cramsan.runasimi.server.dependencyinjection

import com.cramsan.runasimi.server.service.RunasimiService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Class to initialize all the services level components.
 */
internal val ServicesModule = module {
    singleOf(::RunasimiService)
}
