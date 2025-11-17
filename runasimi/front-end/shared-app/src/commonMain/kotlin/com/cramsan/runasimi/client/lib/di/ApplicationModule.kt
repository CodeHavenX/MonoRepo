package com.cramsan.runasimi.client.lib.di

import com.cramsan.architecture.client.di.ApplicationIdentifier
import com.cramsan.architecture.client.di.NamedDependency
import com.cramsan.architecture.client.di.WindowIdentifier
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.runasimi.client.lib.features.application.RunasimiApplicationViewModel
import com.cramsan.runasimi.client.lib.features.window.RunasimiWindowDelegatedEvent
import com.cramsan.runasimi.client.lib.features.window.RunasimiWindowViewModel
import com.cramsan.runasimi.client.lib.init.Initializer
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val ApplicationModule = module {

    single<String>(named(NamedDependency.DOMAIN_KEY)) { "TEMPLATE_REPLACE_ME" }

    singleOf(::Initializer)

    scope<String> {
        scoped(named(WindowIdentifier.DELEGATED_EVENT_BUS)) {
            EventBus<RunasimiWindowDelegatedEvent>()
        } withOptions {
            bind<EventEmitter<RunasimiWindowDelegatedEvent>>()
            bind<EventReceiver<RunasimiWindowDelegatedEvent>>()
        }
    }

    viewModel {
        RunasimiWindowViewModel(
            get(),
            get(named(WindowIdentifier.EVENT_BUS)),
            get(named(WindowIdentifier.DELEGATED_EVENT_BUS)),
        )
    }

    single {
        RunasimiApplicationViewModel(
            get(),
            get(named(ApplicationIdentifier.VIEW_MODEL_DEPENDENCIES)),
        )
    }
}
