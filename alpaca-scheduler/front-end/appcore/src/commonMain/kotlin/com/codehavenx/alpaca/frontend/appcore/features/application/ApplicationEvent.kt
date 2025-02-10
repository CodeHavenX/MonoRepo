package com.codehavenx.alpaca.frontend.appcore.features.application

import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

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
        val id: Int = Random.nextInt(),
    ) : ApplicationEvent()

    /**
     * Navigate back.
     *
     * @param id The id of the event. This is automatically generated. It is made accessible for testing purposes.
     */
    data class NavigateBack(
        val id: Int = Random.nextInt(),
    ) : ApplicationEvent()

    /**
     * Navigate to the [route] from the root page.
     *
     * @param route The route to navigate to.
     * @param id The id of the event. This is automatically generated. It is made accessible for testing purposes.
     */
    data class NavigateFromRootPage(
        val route: String,
        val id: Int = Random.nextInt(),
    ) : ApplicationEvent()

    /**
     * Sign in status change event.
     *
     * @param id The id of the event. This is automatically generated. It is made accessible for testing purposes.
     * @param isSignedIn The sign in status.
     */
    data class SignInStatusChange(
        val id: Int = Random.nextInt(),
        val isSignedIn: Boolean,
    ) : ApplicationEvent()
}
