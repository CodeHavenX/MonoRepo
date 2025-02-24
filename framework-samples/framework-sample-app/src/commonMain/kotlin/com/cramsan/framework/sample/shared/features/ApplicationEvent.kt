package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class ApplicationEvent : ViewModelEvent {

    /**
     * No operation.
     */
    data object Noop : ApplicationEvent()

    /**
     * Navigate to activity.
     */
    data class NavigateToActivity(
        val destination: ActivityDestination,
        val clearTop: Boolean = false,
        val clearStack: Boolean = false,
        val id: Int = Random.nextInt(),
    ) : ApplicationEvent()

    /**
     * Navigate to destination.
     */
    data class NavigateToScreem(
        val destination: Destination,
        val id: Int = Random.nextInt(),
    ) : ApplicationEvent()

    /**
     * Close the activity.
     */
    data class CloseActivity(
        val id: Int = Random.nextInt(),
    ) : ApplicationEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(
        val message: String,
        val id: Int = Random.nextInt(),
    ) : ApplicationEvent()

    /**
     * Navigate back.
     */
    data class NavigateBack(
        val id: Int = Random.nextInt(),
    ) : ApplicationEvent()
}
