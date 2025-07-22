package com.cramsan.framework.sample.shared.di

import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.InvalidEventBus
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.sample.shared.features.ApplicationViewModel
import com.cramsan.framework.sample.shared.features.SampleWindowDelegatedEvent
import com.cramsan.framework.sample.shared.features.main.halt.HaltUtilViewModel
import com.cramsan.framework.sample.shared.features.main.logging.LoggingViewModel
import com.cramsan.framework.sample.shared.features.main.menu.MainMenuViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val ViewModelModule = module {

    scope<String> {
        scoped(named(WindowIdentifier.EVENT_BUS)) {
            EventBus<WindowEvent>()
        } withOptions {
            bind<EventEmitter<WindowEvent>>()
            bind<EventReceiver<WindowEvent>>()
        }

        scoped(named(WindowIdentifier.DELEGATED_EVENT_BUS)) {
            EventBus<SampleWindowDelegatedEvent>()
        } withOptions {
            bind<EventEmitter<SampleWindowDelegatedEvent>>()
            bind<EventReceiver<SampleWindowDelegatedEvent>>()
        }

        scoped {
            ViewModelDependencies(
                get(),
                get(),
                get(),
                get(named(WindowIdentifier.EVENT_BUS)),
                InvalidEventBus(),
            )
        }

        viewModel {
            ApplicationViewModel(
                get(),
                get(),
                get(named(WindowIdentifier.EVENT_BUS)),
            )
        }

        // These objects are scoped to the screen in which they are used.
        viewModelOf(::HaltUtilViewModel)
        viewModelOf(::MainMenuViewModel)
        viewModelOf(::LoggingViewModel)
    }
}

/**
 * Identifiers for various window-level components.
 */
enum class WindowIdentifier {
    EVENT_BUS,
    DELEGATED_EVENT_BUS,
}
