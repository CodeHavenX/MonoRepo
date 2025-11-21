package com.cramsan.runasimi.server.dependencyinjection

import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.runasimi.server.controller.authentication.ContextRetrieverImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Application-level DI module for the runasimi backend.
 */
internal val ApplicationModule = module {
    singleOf(::ContextRetrieverImpl) {
        bind<ContextRetriever<Unit>>()
    }
}
