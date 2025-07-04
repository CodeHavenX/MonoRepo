package com.cramsan.framework.sample.shared.di

import com.cramsan.framework.logging.logE
import com.cramsan.framework.sample.shared.init.Initializer
import com.cramsan.framework.utils.time.Chronos
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal val ExtrasModule = module {

    single<Clock>(createdAtStart = true) {
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
}
