package com.cramsan.flyerboard.client.lib.features.splash

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowNavGraphDestination
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
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
     * The app allows unauthenticated browsing, so we always go to the main graph.
     * Sign-in is accessible from the main nav bar for auth-required features.
     */
    fun navigateToMainScreen() {
        logI(TAG, "Navigating to Main Screen")
        viewModelScope.launch {
            delay(SPLASH_DELAY_MS)
            emitWindowEvent(
                FlyerBoardWindowsEvent.NavigateToNavGraph(
                    FlyerBoardWindowNavGraphDestination.MainNavGraphDestination,
                    clearStack = true,
                )
            )
        }
    }

    companion object {
        private const val TAG = "SplashViewModel"
        private const val SPLASH_DELAY_MS = 1000L
    }
}
