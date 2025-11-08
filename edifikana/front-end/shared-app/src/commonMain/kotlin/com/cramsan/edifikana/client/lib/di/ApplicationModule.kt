package com.cramsan.edifikana.client.lib.di

import com.cramsan.architecture.client.di.ApplicationIdentifier
import com.cramsan.architecture.client.di.NamedDependency
import com.cramsan.architecture.client.di.WindowIdentifier
import com.cramsan.edifikana.client.lib.features.application.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowDelegatedEvent
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowViewModel
import com.cramsan.edifikana.client.lib.init.Initializer
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ApplicationModule = module {

    single<String>(named(NamedDependency.DOMAIN_KEY)) { "EDIFIKANA" }

    singleOf(::Initializer)

    single {
        createJson()
    }

    scope<String> {
        scoped(named(WindowIdentifier.DELEGATED_EVENT_BUS)) {
            EventBus<EdifikanaWindowDelegatedEvent>()
        } withOptions {
            bind<EventEmitter<EdifikanaWindowDelegatedEvent>>()
            bind<EventReceiver<EdifikanaWindowDelegatedEvent>>()
        }
    }

    viewModel {
        EdifikanaWindowViewModel(
            get(),
            get(named(WindowIdentifier.EVENT_BUS)),
            get(named(WindowIdentifier.DELEGATED_EVENT_BUS)),
        )
    }

    single {
        EdifikanaApplicationViewModel(
            get(),
            get(named(ApplicationIdentifier.VIEW_MODEL_DEPENDENCIES)),
            get(),
        )
    }
}
