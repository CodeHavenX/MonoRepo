package com.cramsan.templatereplaceme.client.lib.di

import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.templatereplaceme.client.lib.features.main.menu.MainMenuViewModel
import com.cramsan.templatereplaceme.client.lib.features.splash.SplashViewModel
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowDelegatedEvent
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val ViewModelModule = module {
    viewModelOf(::MainMenuViewModel)
    viewModelOf(::SplashViewModel)
}