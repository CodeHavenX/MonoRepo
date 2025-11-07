package com.cramsan.runasimi.client.lib.di

import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.runasimi.client.lib.features.main.menu.MenuViewModel
import com.cramsan.runasimi.client.lib.features.main.verbs.VerbsViewModel
import com.cramsan.runasimi.client.lib.features.window.RunasimiWindowDelegatedEvent
import com.cramsan.runasimi.client.lib.features.window.RunasimiWindowViewModel
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
            EventBus<RunasimiWindowDelegatedEvent>()
        } withOptions {
            bind<EventEmitter<RunasimiWindowDelegatedEvent>>()
            bind<EventReceiver<RunasimiWindowDelegatedEvent>>()
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

        viewModel {
            RunasimiWindowViewModel(
                get(),
                get(named(WindowIdentifier.EVENT_BUS)),
                get(named(WindowIdentifier.DELEGATED_EVENT_BUS)),
            )
        }

        // These objects are scoped to the screen in which they are used.
        viewModelOf(::VerbsViewModel)
        viewModelOf(::MenuViewModel)
    }
}

/**
 * Identifiers for various window-level components.
 */
enum class WindowIdentifier {
    EVENT_BUS,
    DELEGATED_EVENT_BUS,
}
