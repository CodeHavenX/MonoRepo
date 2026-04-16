package com.cramsan.flyerboard.client.lib.di

import com.cramsan.flyerboard.client.lib.features.auth.sign_in.SignInViewModel
import com.cramsan.flyerboard.client.lib.features.auth.sign_up.SignUpViewModel
import com.cramsan.flyerboard.client.lib.features.main.flyer_list.FlyerListViewModel
import com.cramsan.flyerboard.client.lib.features.main.menu.MainMenuViewModel
import com.cramsan.flyerboard.client.lib.features.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val ViewModelModule = module {
    scope<String> {
        viewModelOf(::SplashViewModel)
        viewModelOf(::SignInViewModel)
        viewModelOf(::SignUpViewModel)
        viewModelOf(::FlyerListViewModel)
        viewModelOf(::MainMenuViewModel)
    }
}
