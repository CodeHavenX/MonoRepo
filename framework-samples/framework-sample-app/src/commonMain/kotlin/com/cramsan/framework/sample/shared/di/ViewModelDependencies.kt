package com.cramsan.framework.sample.shared.di

import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.sample.shared.features.ApplicationViewModel
import com.cramsan.framework.sample.shared.features.main.halt.HaltUtilViewModel
import com.cramsan.framework.sample.shared.features.main.menu.MainMenuViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val ViewModelModule = module {

    // These objects are singletons and are not scoped to any particular navigation graph.
    singleOf(::ViewModelDependencies)
    singleOf(::ApplicationViewModel)

    // These objects are scoped to the screen in which they are used.
    viewModelOf(::HaltUtilViewModel)
    viewModelOf(::MainMenuViewModel)
}
