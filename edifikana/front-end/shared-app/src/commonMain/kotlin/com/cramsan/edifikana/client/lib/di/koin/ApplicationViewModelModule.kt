package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.ApplicationEventBus
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

    single(named(PROCESS_VIEW_MODEL_SCOPE)) {
        ApplicationEventBus()
    } withOptions {
        bind<EventReceiver<ApplicationEvent>>()
        bind<EventEmitter<ApplicationEvent>>()
    }

    single(named(PROCESS_VIEW_MODEL_SCOPE)) {
        InvalidEventBus<WindowEvent>()
    } withOptions {
        bind<EventReceiver<WindowEvent>>()
        bind<EventEmitter<WindowEvent>>()
    }

    single(named(PROCESS_VIEW_MODEL_SCOPE)) {
        ViewModelDependencies(
            get(),
            get(),
            get(),
            get(named(PROCESS_VIEW_MODEL_SCOPE)),
            get(named(PROCESS_VIEW_MODEL_SCOPE)),
        )
    }

    single {
        EdifikanaApplicationViewModel(
            get(),
            get(named(PROCESS_VIEW_MODEL_SCOPE)),
            get(),
        )
    }
}

private const val PROCESS_VIEW_MODEL_SCOPE = "process_view_model_scope"
