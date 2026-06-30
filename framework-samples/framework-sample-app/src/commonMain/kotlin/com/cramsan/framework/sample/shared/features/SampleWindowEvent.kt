package com.cramsan.framework.sample.shared.features

import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.core.compose.navigation.NavResultKey

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class SampleWindowEvent : WindowEvent {
    /**
     * Navigate to nav graph.
     */
    data class NavigateToNavGraph(
        val destination: ApplicationNavGraphDestination,
        val clearTop: Boolean = false,
        val clearStack: Boolean = false,
    ) : SampleWindowEvent()

    /**
     * Navigate to destination.
     */
    data class NavigateToScreen(val destination: Destination) : SampleWindowEvent()

    /**
     * Close the nav graph.
     */
    data object CloseNavGraph : SampleWindowEvent()

    /**
     * Show a snackbar.
     */
    data class ShowSnackbar(val message: String) : SampleWindowEvent()

    /**
     * Navigate back.
     */
    data object NavigateBack : SampleWindowEvent()

    /**
     * Navigate back and deposit a typed result into the previous back-stack entry's
     * SavedStateHandle, where it can be consumed via ObserveNavResult.
     *
     * Prefer constructing this via [NavResultKey.navigateBackWith] rather than directly.
     */
    data class NavigateBackWithResult(
        val resultKey: String,
        val resultValue: Any,
    ) : SampleWindowEvent()
}

/**
 * Creates a [SampleWindowEvent.NavigateBackWithResult] for this key and [value].
 */
fun <T : Any> NavResultKey<T>.navigateBackWith(value: T): SampleWindowEvent.NavigateBackWithResult =
    SampleWindowEvent.NavigateBackWithResult(resultKey = name, resultValue = value)
