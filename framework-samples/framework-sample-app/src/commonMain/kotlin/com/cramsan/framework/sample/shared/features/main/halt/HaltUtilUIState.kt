package com.cramsan.framework.sample.shared.features.main.halt

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the HaltUtil feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class HaltUtilUIState(val isLoading: Boolean) : ViewModelUIState {
    companion object {
        val Initial = HaltUtilUIState(false)
    }
}
