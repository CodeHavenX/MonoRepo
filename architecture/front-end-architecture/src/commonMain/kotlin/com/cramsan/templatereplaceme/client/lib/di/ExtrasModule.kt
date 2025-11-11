package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.InvalidEventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.resources.ComposeStringProvider
import com.cramsan.framework.core.compose.resources.StringProvider
import com.cramsan.framework.logging.logE
import com.cramsan.framework.utils.time.Chronos
import com.cramsan.templatereplaceme.client.lib.settings.SettingsHolder
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Koin module for initializing additional application-level dependencies for front-end applications.
 * This module provides components like clock implementations, exception handlers, coroutine scopes,
 * string providers, settings holders, event buses, and view model dependencies.
 */
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

    singleOf(::ComposeStringProvider) {
        bind<StringProvider>()
    }

    single {
        SettingsHolder(get())
    }

    single { ManagerDependencies(get(), get()) }

    single(named(ApplicationIdentifier.EVENT_BUS)) {
        EventBus<ApplicationEvent>()
    } withOptions {
        bind<EventReceiver<ApplicationEvent>>()
        bind<EventEmitter<ApplicationEvent>>()
    }

    single(named(ApplicationIdentifier.WINDOW_EVENT_BUS)) {
        InvalidEventBus<WindowEvent>()
    } withOptions {
        bind<EventReceiver<WindowEvent>>()
        bind<EventEmitter<WindowEvent>>()
    }

    single(named(ApplicationIdentifier.VIEW_MODEL_DEPENDENCIES)) {
        ViewModelDependencies(
            get(),
            get(),
            get(),
            get(named(ApplicationIdentifier.WINDOW_EVENT_BUS)),
            get(named(ApplicationIdentifier.EVENT_BUS)),
        )
    }

    scope<String> {
        scoped(named(WindowIdentifier.EVENT_BUS)) {
            EventBus<WindowEvent>()
        } withOptions {
            bind<EventEmitter<WindowEvent>>()
            bind<EventReceiver<WindowEvent>>()
        }

        scoped {
            ViewModelDependencies(
                get(),
                get(),
                get(),
                get(named(WindowIdentifier.EVENT_BUS)),
                get(named(ApplicationIdentifier.EVENT_BUS)),
            )
        }
    }
}

/**
 * Identifiers for various window-level components.
 */
enum class WindowIdentifier {
    EVENT_BUS,
    DELEGATED_EVENT_BUS,
}

/**
 * Identifiers for various application-level components.
 */
enum class ApplicationIdentifier {
    EVENT_BUS,
    WINDOW_EVENT_BUS,
    VIEW_MODEL_DEPENDENCIES,
}
