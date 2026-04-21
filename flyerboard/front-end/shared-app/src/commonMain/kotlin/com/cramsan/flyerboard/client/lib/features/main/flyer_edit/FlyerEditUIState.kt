package com.cramsan.flyerboard.client.lib.features.main.flyer_edit

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Flyer Edit screen.
 */
data class FlyerEditUIState(
    val isLoading: Boolean,
    val isSaving: Boolean,
    val title: String,
    val description: String,
    val expiresAt: String?,
    val errorMessage: String?,
) : ViewModelUIState {
    companion object {
        val Initial = FlyerEditUIState(
            isLoading = false,
            isSaving = false,
            title = "",
            description = "",
            expiresAt = null,
            errorMessage = null,
        )
    }
}
