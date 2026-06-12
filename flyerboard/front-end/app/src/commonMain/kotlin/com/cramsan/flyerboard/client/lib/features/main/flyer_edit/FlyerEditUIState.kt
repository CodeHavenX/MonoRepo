package com.cramsan.flyerboard.client.lib.features.main.flyer_edit

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the Flyer Edit screen.
 */
sealed class FlyerEditUIState : ViewModelUIState {
    /**
     * Loading state
     */
    data object Loading : FlyerEditUIState()

    /**
     * DData class for editing the flyer.
     */
    data class Editing(
        val title: String,
        val description: String,
        val expiresAt: String?,
        val errorMessage: String?,
        val selectedFileName: String?,
        val isSaving: Boolean,
    ) : FlyerEditUIState()

    companion object {
        val Initial: FlyerEditUIState = Loading
    }
}
