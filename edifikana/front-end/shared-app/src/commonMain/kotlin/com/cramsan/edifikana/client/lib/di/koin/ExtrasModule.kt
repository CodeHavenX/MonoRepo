package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.init.Initializer
import com.cramsan.framework.logging.logE
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.datetime.Clock
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ExtrasModule = module {

    single<Clock> { Clock.System }

    single {
        CoroutineExceptionHandler { _, throwable ->
            logE("CoroutineExceptionHandler", "Uncaught Exception", throwable)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    single<CoroutineScope> { GlobalScope }

    singleOf(::Initializer)
}
