package com.codehavenx.alpaca.frontend.appcore.features.application

import com.cramsan.framework.core.compose.ViewModelEvent
import com.cramsan.framework.core.compose.WindowEvent

/**
 * This file contains the application level events that can be handled.
 */
sealed class AlpacaWindowEvent : WindowEvent {

    /**
     * Navigate to a route.
     *
     * @param route The route to navigate to.
     * @param id The id of the event. This is automatically generated. It is made accessible for testing purposes.
     */
    data class Navigate(
        val route: String,
    ) : AlpacaWindowEvent()

    /**
     * Navigate back.
     *
     * @param id The id of the event. This is automatically generated. It is made accessible for testing purposes.
     */
    data object NavigateBack : AlpacaWindowEvent()

    /**
     * Navigate to the [route] from the root page.
     *
     * @param route The route to navigate to.
     * @param id The id of the event. This is automatically generated. It is made accessible for testing purposes.
     */
    data class NavigateFromRootPage(
        val route: String,
    ) : AlpacaWindowEvent()

    /**
     * Sign in status change event.
     *
     * @param id The id of the event. This is automatically generated. It is made accessible for testing purposes.
     * @param isSignedIn The sign in status.
     */
    data class SignInStatusChange(
        val isSignedIn: Boolean,
    ) : AlpacaWindowEvent()
}

/**
 * Events that can be triggered in the whole application. These events are intended to be
 * triggered by a feature screen, and it will be handled by the application.
 */
sealed class AlpacaApplicationViewModelEvent : ViewModelEvent {
    /**
     * Wrapper for [AlpacaWindowEvent] to be used in the view model.
     */
    data class AlpacaApplicationEventWrapper(
        val event: AlpacaWindowEvent,
    ) : AlpacaApplicationViewModelEvent()
}
