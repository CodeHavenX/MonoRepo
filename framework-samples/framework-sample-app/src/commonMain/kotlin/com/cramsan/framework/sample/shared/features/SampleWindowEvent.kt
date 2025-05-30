package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.core.compose.WindowEvent

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class SampleWindowEvent : WindowEvent {

    /**
     * Navigate to activity.
     */
    data class NavigateToActivity(
        val destination: ActivityDestination,
        val clearTop: Boolean = false,
        val clearStack: Boolean = false,
    ) : SampleWindowEvent()

    /**
     * Navigate to destination.
     */
    data class NavigateToScreen(
        val destination: Destination,
    ) : SampleWindowEvent()

    /**
     * Close the activity.
     */
    data object CloseActivity : SampleWindowEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(
        val message: String,
    ) : SampleWindowEvent()

    /**
     * Navigate back.
     */
    data object NavigateBack : SampleWindowEvent()
}
