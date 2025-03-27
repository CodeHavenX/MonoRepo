package com.cramsan.edifikana.client.lib.features.splash

import com.cramsan.edifikana.client.lib.features.ActivityDestination
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationEvent
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
            emitEvent(SplashEvent.TriggerApplicationEvent(EdifikanaApplicationEvent.NavigateBack))
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
        emitEvent(
            SplashEvent.TriggerApplicationEvent(
                EdifikanaApplicationEvent.NavigateToActivity(ActivityDestination.MainDestination)
            )
        )
    }

    private suspend fun navigateToSignInScreen() {
        propertyManager.setActiveProperty(null)
        emitEvent(
            SplashEvent.TriggerApplicationEvent(
                EdifikanaApplicationEvent.NavigateToActivity(
                    ActivityDestination.AuthDestination,
                    clearStack = true,
                )
            )
        )
    }

    companion object {
        private const val TAG = "SplashViewModel"
    }
}
