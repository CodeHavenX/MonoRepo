package com.cramsan.edifikana.client.lib.features.splash

import com.cramsan.edifikana.client.lib.features.auth.AuthDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.edifikana.client.lib.managers.OrganizationManager
import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.logging.logI
import com.cramsan.framework.logging.logW
import kotlinx.coroutines.launch

/**
 * ViewModel for the Splash screen.
 **/
@FrontendViewModel
class SplashViewModel(
    dependencies: ViewModelDependencies,
    private val authManager: AuthManager,
    private val organizationManager: OrganizationManager,
) : BaseViewModel<SplashEvent, SplashUIState>(
    dependencies,
    SplashUIState.Initial,
    TAG,
) {
    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelCoroutineScope.launch {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateBack)
        }
    }

    /**
     * Enforce the authentication state and route the user to the right screen.
     * If [initialDestination] is non-null and auth succeeds, navigates to that destination
     * on top of the Home nav graph instead of stopping at the default hub screen.
     */
    fun enforceAuth(initialDestination: Destination? = null) =
        viewModelCoroutineScope.launch {
            val result = authManager.isSignedIn()
            if (result.isFailure) {
                logW(TAG, "Failure when enforcing auth.", result.exceptionOrNull())
                navigateToSignInScreen()
            } else {
                logI(TAG, "EnforceAuth result: ${result.getOrThrow()}")
                if (!result.getOrThrow()) {
                    navigateToSignInScreen()
                } else {
                    val orgResult = organizationManager.getOrganizations()
                    val orgs = orgResult.getOrNull()
                    if (orgs.isNullOrEmpty()) {
                        navigateToOnboardingScreen()
                    } else {
                        navigateToMainScreen(initialDestination)
                    }
                }
            }
        }

    private suspend fun navigateToMainScreen(initialDestination: Destination? = null) {
        emitWindowEvent(
            EdifikanaWindowsEvent.NavigateToNavGraph(EdifikanaNavGraphDestination.HomeNavGraphDestination),
        )
        if (initialDestination != null) {
            emitWindowEvent(EdifikanaWindowsEvent.NavigateToScreen(initialDestination))
        }
    }

    private suspend fun navigateToSignInScreen() {
        emitWindowEvent(
            EdifikanaWindowsEvent.NavigateToNavGraph(
                EdifikanaNavGraphDestination.AuthNavGraphDestination,
                clearStack = true,
            ),
        )
    }

    private suspend fun navigateToOnboardingScreen() {
        emitWindowEvent(
            EdifikanaWindowsEvent.NavigateToScreen(
                AuthDestination.SelectOrgDestination,
                clearStack = true,
            ),
        )
    }

    companion object {
        private const val TAG = "SplashViewModel"
    }
}
