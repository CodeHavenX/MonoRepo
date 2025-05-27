package com.cramsan.edifikana.client.lib.features.auth.validation

import com.cramsan.edifikana.client.lib.managers.AuthManager
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Validation screen.
 **/
class ValidationViewModel(
    dependencies: ViewModelDependencies,
    private val auth: AuthManager,
) : BaseViewModel<ValidationEvent, ValidationUIState>(
    dependencies,
    ValidationUIState.Initial,
    TAG,
) {
    /**
     * Verify the account has been created, has required fields.
     */
    fun verifyAccount() {
        viewModelScope.launch {
            auth.getUser().getOrThrow()
        }
    }

    /**
     * Sign the user in with a magic link.
     */
    fun signInWithMagicLink(email: String, hashToken: String) {
        viewModelScope.launch {
            auth.signInWithMagicLink(email, hashToken)
        }
    }

    companion object {
        private const val TAG = "ValidationViewModel"
    }
}
