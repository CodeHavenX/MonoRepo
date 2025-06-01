package com.cramsan.edifikana.client.lib.features.management.addproperty

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the AddProperty feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class AddPropertyUIState(
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = AddPropertyUIState(false)
    }
}
