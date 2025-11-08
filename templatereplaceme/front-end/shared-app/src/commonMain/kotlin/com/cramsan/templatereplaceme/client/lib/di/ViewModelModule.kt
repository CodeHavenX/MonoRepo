package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.templatereplaceme.client.lib.features.main.menu.MainMenuViewModel
import com.cramsan.templatereplaceme.client.lib.features.splash.SplashViewModel
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowDelegatedEvent
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowViewModel
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
            EventBus<TemplateReplaceMeWindowDelegatedEvent>()
        } withOptions {
            bind<EventEmitter<TemplateReplaceMeWindowDelegatedEvent>>()
            bind<EventReceiver<TemplateReplaceMeWindowDelegatedEvent>>()
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
            TemplateReplaceMeWindowViewModel(
                get(),
                get(named(WindowIdentifier.EVENT_BUS)),
                get(named(WindowIdentifier.DELEGATED_EVENT_BUS)),
            )
        }

        // These objects are scoped to the screen in which they are used.
        viewModelOf(::MainMenuViewModel)
        viewModelOf(::SplashViewModel)
    }
}

/**
 * Identifiers for various window-level components.
 */
enum class WindowIdentifier {
    EVENT_BUS,
    DELEGATED_EVENT_BUS,
}
