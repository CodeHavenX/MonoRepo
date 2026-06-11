package com.cramsan.flyerboard.client.lib.features.splash

import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowNavGraphDestination
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel for the Splash screen.
 **/
@FrontendViewModel
class SplashViewModel(dependencies: ViewModelDependencies) :
    BaseViewModel<SplashEvent, SplashUIState>(
        dependencies,
        SplashUIState,
        TAG,
    ) {
    /**
     * Navigate to the Main Screen after splash.
     * The app allows unauthenticated browsing, so we always go to the main graph.
     * Sign-in is accessible from the main nav bar for auth-required features.
     *
     * If [initialDestination] is non-null, navigates there after the main graph loads
     * to honour deep-links from the browser URL bar.
     */
    fun navigateToMainScreen(initialDestination: Destination? = null) {
        logI(TAG, "Navigating to Main Screen")
        viewModelCoroutineScope.launch {
            delay(SPLASH_DELAY_MS)
            emitWindowEvent(
                FlyerBoardWindowsEvent.NavigateToNavGraph(
                    FlyerBoardWindowNavGraphDestination.MainNavGraphDestination,
                    clearStack = true,
                ),
            )
            if (initialDestination != null) {
                emitWindowEvent(FlyerBoardWindowsEvent.NavigateToScreen(initialDestination))
            }
        }
    }

    companion object {
        private const val TAG = "SplashViewModel"
        private const val SPLASH_DELAY_MS = 1000L
    }
}
