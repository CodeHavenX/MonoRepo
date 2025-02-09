package com.cramsan.edifikana.client.lib.features.admin.property

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Property feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class PropertyUIState(
    val content: PropertyUIModel?,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Empty = PropertyUIState(null, false)
    }
}
