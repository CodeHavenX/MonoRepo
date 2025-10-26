package com.cramsan.runasimi.client.lib.di

import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.InvalidEventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.runasimi.client.lib.features.application.RunasimiApplicationViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val ApplicationViewModelModule = module {

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

    single {
        RunasimiApplicationViewModel(
            get(),
            get(named(ApplicationIdentifier.VIEW_MODEL_DEPENDENCIES)),
        )
    }
}

/**
 * Identifiers for various application-level components.
 */
enum class ApplicationIdentifier {
    EVENT_BUS,
    WINDOW_EVENT_BUS,
    VIEW_MODEL_DEPENDENCIES,
}
