package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.core.compose.ApplicationEvent

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class SampleApplicationEvent : ApplicationEvent {

    /**
     * Navigate to activity.
     */
    data class NavigateToActivity(
        val destination: ActivityDestination,
        val clearTop: Boolean = false,
        val clearStack: Boolean = false,
    ) : SampleApplicationEvent()

    /**
     * Navigate to destination.
     */
    data class NavigateToScreen(
        val destination: Destination,
    ) : SampleApplicationEvent()

    /**
     * Close the activity.
     */
    data object CloseActivity : SampleApplicationEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(
        val message: String,
    ) : SampleApplicationEvent()

    /**
     * Navigate back.
     */
    data object NavigateBack : SampleApplicationEvent()
}
