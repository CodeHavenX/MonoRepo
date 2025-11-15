package com.cramsan.edifikana.client.lib.di

import com.cramsan.architecture.client.di.ApplicationIdentifier
import com.cramsan.architecture.client.di.NamedDependency
import com.cramsan.architecture.client.di.WindowIdentifier
import com.cramsan.edifikana.client.lib.features.application.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowViewModel
import com.cramsan.edifikana.client.lib.init.Initializer
import com.cramsan.edifikana.lib.serialization.createJson
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val ApplicationModule = module {

    single<String>(named(NamedDependency.DOMAIN_KEY)) { "EDIFIKANA" }

    singleOf(::Initializer)

    single {
        createJson()
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
