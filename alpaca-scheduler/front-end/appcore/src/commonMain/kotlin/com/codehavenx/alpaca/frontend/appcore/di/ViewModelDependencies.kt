package com.codehavenx.alpaca.frontend.appcore.di

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationViewModel
import com.codehavenx.alpaca.frontend.appcore.features.main.MainMenuViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ViewModelModule = module {

    // TODO: Currently we cannot scope a viewmodel to a navigation graph until koin supports the viewModel function
    // in compose multiplatform code. Until then we will have all viewmodels as singletons.
    singleOf(::ApplicationViewModel)
    singleOf(::MainMenuViewModel)
}
