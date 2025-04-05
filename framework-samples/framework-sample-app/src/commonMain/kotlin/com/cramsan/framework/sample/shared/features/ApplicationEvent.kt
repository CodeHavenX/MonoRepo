package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.core.compose.ApplicationViewModelEvent

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class ApplicationEvent : ApplicationViewModelEvent {

    /**
     * Navigate to activity.
     */
    data class NavigateToActivity(
        val destination: ActivityDestination,
        val clearTop: Boolean = false,
        val clearStack: Boolean = false,
    ) : ApplicationEvent()

    /**
     * Navigate to destination.
     */
    data class NavigateToScreen(
        val destination: Destination,
    ) : ApplicationEvent()

    /**
     * Close the activity.
     */
    data object CloseActivity : ApplicationEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(
        val message: String,
    ) : ApplicationEvent()

    /**
     * Navigate back.
     */
    data object NavigateBack : ApplicationEvent()
}
