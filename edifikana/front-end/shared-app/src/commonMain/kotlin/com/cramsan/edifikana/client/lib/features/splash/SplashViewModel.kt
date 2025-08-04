package com.cramsan.edifikana.client.lib.features.splash

import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.PropertyManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.launch

/**
 * ViewModel for the Splash screen.
 **/
class SplashViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
    private val propertyManager: PropertyManager,
) : BaseViewModel<SplashEvent, SplashUIState>(
    dependencies,
    SplashUIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Enforce the authentication state and route the user to the right screen.
     */
    fun enforceAuth() = viewModelScope.launch {
        val result = authManager.isSignedIn()
        if (result.isFailure) {
            logW(TAG, "Failure when enforcing auth.", result.exceptionOrNull())
            navigateToSignInScreen()
        } else {
            logI(TAG, "EnforceAuth result: ${result.getOrThrow()}")
            if (!result.getOrThrow()) {
                navigateToSignInScreen()
            } else {
                navigateToMainScreen()
            }
        }
    }

    private suspend fun navigateToMainScreen() {
        val propertyResult = propertyManager.getPropertyList()
        propertyManager.setActiveProperty(propertyResult.getOrNull()?.firstOrNull()?.id)
        emitWindowEvent(
            EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.ManagementNavGraphDestination)
        )
    }

    private suspend fun navigateToSignInScreen() {
        propertyManager.setActiveProperty(null)
        emitWindowEvent(
            EdifikanaWindowsEvent.NavigateToNavGraph(
                EdifikanaNavGraphDestination.AuthNavGraphDestination,
                clearStack = true,
            )
        )
    }

    companion object {
        private const val TAG = "SplashViewModel"
    }
}
