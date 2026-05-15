package com.cramsan.flyerboard.client.lib.features.main.flyer_submit

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * Mutually exclusive submission states for the Flyer Submit screen.
 */
sealed class SubmitStatus {
    /** The form is idle and ready for input. */
    data object Idle : SubmitStatus()

    /** A submission is currently in progress. */
    data object Submitting : SubmitStatus()

    /** The most recent submission attempt failed with [message]. */
    data class Failed(val message: String) : SubmitStatus()
}

/**
 * UI state for the Flyer Submit screen.
 */
data class FlyerSubmitUIState(
    val status: SubmitStatus,
    val title: String,
    val description: String,
    val expiresAt: String?,
    val selectedFileName: String?,
) : ViewModelUIState {
    companion object {
        val Initial =
            FlyerSubmitUIState(
                status = SubmitStatus.Idle,
                title = "",
                description = "",
                expiresAt = null,
                selectedFileName = null,
            )
    }
}
