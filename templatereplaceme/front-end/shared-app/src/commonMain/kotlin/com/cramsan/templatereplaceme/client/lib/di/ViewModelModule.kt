package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.templatereplaceme.client.lib.features.main.menu.MainMenuViewModel
import com.cramsan.templatereplaceme.client.lib.features.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val ViewModelModule = module {
    scope<String> {
        viewModelOf(::MainMenuViewModel)
        viewModelOf(::SplashViewModel)
    }
}
