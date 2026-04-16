package com.cramsan.flyerboard.client.lib.di

import com.cramsan.architecture.client.di.ApplicationIdentifier
import com.cramsan.architecture.client.di.NamedDependency
import com.cramsan.architecture.client.di.WindowIdentifier
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.flyerboard.client.lib.features.application.FlyerBoardApplicationViewModel
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowDelegatedEvent
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowViewModel
import com.cramsan.flyerboard.client.lib.init.Initializer
import com.cramsan.flyerboard.lib.serialization.createJson
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val ApplicationModule = module {

    single<String>(named(NamedDependency.DOMAIN_KEY)) { "flyerboard" }

    single {
        Initializer()
    }

    single {
        createJson()
    }

    scope<String> {
        scoped(named(WindowIdentifier.DELEGATED_EVENT_BUS)) {
            EventBus<FlyerBoardWindowDelegatedEvent>()
        } withOptions {
            bind<EventEmitter<FlyerBoardWindowDelegatedEvent>>()
            bind<EventReceiver<FlyerBoardWindowDelegatedEvent>>()
        }

        viewModel {
            FlyerBoardWindowViewModel(
                get(),
                get(named(WindowIdentifier.EVENT_BUS)),
                get(named(WindowIdentifier.DELEGATED_EVENT_BUS)),
                get(),
            )
        }
    }

    single {
        FlyerBoardApplicationViewModel(
            get(),
            get(named(ApplicationIdentifier.VIEW_MODEL_DEPENDENCIES)),
        )
    }
}
