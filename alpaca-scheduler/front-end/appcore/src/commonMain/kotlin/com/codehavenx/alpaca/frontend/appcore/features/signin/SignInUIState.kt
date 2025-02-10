package com.codehavenx.alpaca.frontend.appcore.features.signin

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI Model for the Sign In screen.
 */
data class SignInUIState(
    val content: SignInUIModel,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = SignInUIState(
            content = SignInUIModel("", "", error = false),
            isLoading = true,
        )
    }
}
