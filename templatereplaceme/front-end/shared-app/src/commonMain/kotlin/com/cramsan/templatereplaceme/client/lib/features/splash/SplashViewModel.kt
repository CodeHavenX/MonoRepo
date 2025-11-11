package com.cramsan.templatereplaceme.client.lib.features.splash

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
class SplashViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<SplashEvent, SplashUIState>(
    dependencies,
    SplashUIState.Initial,
    TAG,
) {

    /**
     * Navigate to the Main Screen after splash.
     */
    fun navigateToMainScreen() {
        logI(TAG, "Navigating to Main Screen")
        viewModelScope.launch {
            @Suppress("MagicNumber")
            delay(1000) // Simulate loading time
            emitWindowEvent(
                TemplateReplaceMeWindowsEvent.NavigateToNavGraph(
                    TemplateReplaceMeWindowNavGraphDestination.AuthNavGraphDestination
                )
            )
        }
    }

    companion object {
        private const val TAG = "SplashViewModel"
    }
}
