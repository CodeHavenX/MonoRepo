package com.cramsan.templatereplaceme.client.lib.features.splash

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowNavGraphDestination
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowsEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel for the Splash screen.
 **/
@FrontendViewModel
class SplashViewModel(dependencies: ViewModelDependencies) :
    BaseViewModel<SplashEvent, SplashUIState>(
        dependencies,
        SplashUIState.Initial,
        TAG,
    ) {

    companion object {
        private const val TAG = "SplashViewModel"
    }
}
