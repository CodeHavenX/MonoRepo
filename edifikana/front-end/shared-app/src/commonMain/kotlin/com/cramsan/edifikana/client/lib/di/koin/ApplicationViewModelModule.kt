package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.InvalidEventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val ApplicationViewModelModule = module {

    single(named(APPLICATION_EVENT_BUS)) {
        EventBus<ApplicationEvent>()
    } withOptions {
        bind<EventReceiver<ApplicationEvent>>()
        bind<EventEmitter<ApplicationEvent>>()
    }

    single(named(APPLICATION_WINDOW_EVENT_BUS)) {
        InvalidEventBus<WindowEvent>()
    } withOptions {
        bind<EventReceiver<WindowEvent>>()
        bind<EventEmitter<WindowEvent>>()
    }

    single(named(APPLICATION_VIEW_MODEL_DEPENDENCIES)) {
        ViewModelDependencies(
            get(),
            get(),
            get(),
            get(named(APPLICATION_WINDOW_EVENT_BUS)),
            get(named(APPLICATION_EVENT_BUS)),
        )
    }

    single {
        EdifikanaApplicationViewModel(
            get(),
            get(named(APPLICATION_VIEW_MODEL_DEPENDENCIES)),
            get(),
        )
    }
}

const val APPLICATION_EVENT_BUS = "applicationEventBus"
private const val APPLICATION_WINDOW_EVENT_BUS = "applicationWindowEventBus"
private const val APPLICATION_VIEW_MODEL_DEPENDENCIES = "applicationViewModelDependencies"
