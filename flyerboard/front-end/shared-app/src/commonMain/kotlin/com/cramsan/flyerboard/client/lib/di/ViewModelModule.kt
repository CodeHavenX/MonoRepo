package com.cramsan.flyerboard.client.lib.di

import com.cramsan.flyerboard.client.lib.features.main.menu.MainMenuViewModel
import com.cramsan.flyerboard.client.lib.features.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val ViewModelModule = module {
    scope<String> {
        viewModelOf(::MainMenuViewModel)
        viewModelOf(::SplashViewModel)
    }
}
