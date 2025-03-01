package com.cramsan.edifikana.client.lib.features.admin.property

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Property feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class PropertyUIState(
    val propertyName: String?,
    val address: String?,
    val isLoading: Boolean,
    val managers: List<String>,
    val addManagerError: Boolean,
    val addManagerEmail: String,
) : ViewModelUIState {
    companion object {
        val Empty = PropertyUIState(
            propertyName = null,
            address = null,
            isLoading = false,
            managers = emptyList(),
            addManagerError = false,
            addManagerEmail = "",
        )
    }
}
