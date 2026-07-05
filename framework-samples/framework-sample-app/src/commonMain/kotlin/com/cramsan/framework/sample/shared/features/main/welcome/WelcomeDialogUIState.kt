package com.cramsan.framework.sample.shared.features.main.welcome

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the WelcomeDialog screen.
 */
data object WelcomeDialogUIState : ViewModelUIState {
    val Initial: WelcomeDialogUIState get() = this
}
