package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.architecture.client.di.ApplicationIdentifier
import com.cramsan.architecture.client.di.NamedDependency
import com.cramsan.architecture.client.di.WindowIdentifier
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.templatereplaceme.client.lib.features.application.TemplateReplaceMeApplicationViewModel
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowDelegatedEvent
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowViewModel
import com.cramsan.templatereplaceme.client.lib.init.Initializer
import com.cramsan.templatereplaceme.lib.serialization.createJson
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val ApplicationModule = module {

    single<String>(named(NamedDependency.DOMAIN_KEY)) { "TEMPLATE_REPLACE_ME" }

    single {
        Initializer()
    }

    single {
        createJson()
    }

    scope<String> {
        scoped(named(WindowIdentifier.DELEGATED_EVENT_BUS)) {
            EventBus<TemplateReplaceMeWindowDelegatedEvent>()
        } withOptions {
            bind<EventEmitter<TemplateReplaceMeWindowDelegatedEvent>>()
            bind<EventReceiver<TemplateReplaceMeWindowDelegatedEvent>>()
        }
    }

    viewModel {
        TemplateReplaceMeWindowViewModel(
            get(),
            get(named(WindowIdentifier.EVENT_BUS)),
            get(named(WindowIdentifier.DELEGATED_EVENT_BUS)),
        )
    }

    single {
        TemplateReplaceMeApplicationViewModel(
            get(),
            get(named(ApplicationIdentifier.VIEW_MODEL_DEPENDENCIES)),
        )
    }
}
