package com.cramsan.edifikana.client.lib.features.home.properties

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the PropertyManager feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class PropertyManagerUIState(
    val content: PropertyManagerUIModel,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Empty = PropertyManagerUIState(
            content = PropertyManagerUIModel(emptyList()),
            isLoading = false,
        )
    }
}
