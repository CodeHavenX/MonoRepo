package com.cramsan.architecture.server.dependencyinjection

import com.cramsan.framework.core.ktor.Controller
import org.koin.dsl.module

/**
 * Class to initialize and bind the ktor components.
 */
internal val KtorModule = module {

    single<List<Controller>> {
        getAll<Controller>()
    }
}
