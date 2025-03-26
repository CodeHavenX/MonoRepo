package com.codehavenx.alpaca.frontend.appcore.features.application

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * This file contains the application level events that can be handled.
 */
sealed class ApplicationEvent : ViewModelEvent {
    /**
     * No operation event.
     */
    data object Noop : ApplicationEvent()

    /**
     * Navigate to a route.
     *
     * @param route The route to navigate to.
     * @param id The id of the event. This is automatically generated. It is made accessible for testing purposes.
     */
    data class Navigate(
        val route: String,
    ) : ApplicationEvent()

    /**
     * Navigate back.
     *
     * @param id The id of the event. This is automatically generated. It is made accessible for testing purposes.
     */
    data object NavigateBack : ApplicationEvent()

    /**
     * Navigate to the [route] from the root page.
     *
     * @param route The route to navigate to.
     * @param id The id of the event. This is automatically generated. It is made accessible for testing purposes.
     */
    data class NavigateFromRootPage(
        val route: String,
    ) : ApplicationEvent()

    /**
     * Sign in status change event.
     *
     * @param id The id of the event. This is automatically generated. It is made accessible for testing purposes.
     * @param isSignedIn The sign in status.
     */
    data class SignInStatusChange(
        val isSignedIn: Boolean,
    ) : ApplicationEvent()
}
