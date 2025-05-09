package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.init.Initializer
import com.cramsan.framework.core.compose.resources.ComposeStringProvider
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.logE
import com.cramsan.framework.utils.time.Chronos
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.datetime.Clock
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ExtrasModule = module {

    single<Clock> {
        Chronos.initializeClock(clock = Clock.System)
        Chronos.clock()
    }

    single {
        CoroutineExceptionHandler { _, throwable ->
            logE("CoroutineExceptionHandler", "Uncaught Exception", throwable)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    single<CoroutineScope> { GlobalScope }

    singleOf(::Initializer)

    singleOf(::ComposeStringProvider) {
        bind<StringProvider>()
    }
}
