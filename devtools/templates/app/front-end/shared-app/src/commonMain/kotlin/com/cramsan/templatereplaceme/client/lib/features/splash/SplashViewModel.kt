package com.cramsan.templatereplaceme.client.lib.features.splash

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.logging.logI
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

    /**
     * Navigate to the main screen after splash.
     *
     * If [initialDestination] is non-null, navigates there after the main graph loads to honour
     * deep-links from the browser URL bar.
     *
     * ⚠️ WARNING: Without the NavigateToNavGraph call below the app will hang on the splash screen.
     * After running `create activity`, replace the TODO with:
     * ```
     * emitWindowEvent(
     *     TemplateReplaceMeWindowsEvent.NavigateToNavGraph(
     *         TemplateReplaceMeWindowNavGraphDestination.MainNavGraphDestination,
     *         clearStack = true,
     *     ),
     * )
     * ```
     * where `MainNavGraphDestination` is the entry you added to
     * [TemplateReplaceMeWindowNavGraphDestination] for your first activity.
     */
    fun navigateToMainScreen(initialDestination: Destination? = null) {
        logI(TAG, "Navigating to main screen")
        viewModelCoroutineScope.launch {
            delay(SPLASH_DELAY_MS)
            // TODO: Emit NavigateToNavGraph to your app's first activity nav graph (see KDoc above).
            //       Without this the app will stay on the splash screen indefinitely.
            if (initialDestination != null) {
                emitWindowEvent(TemplateReplaceMeWindowsEvent.NavigateToScreen(initialDestination))
            }
        }
    }

    companion object {
        private const val TAG = "SplashViewModel"
        private const val SPLASH_DELAY_MS = 1000L
    }
}
