package com.cramsan.templatereplaceme.client.lib.features.splash

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the Splash feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class SplashEvent : ViewModelEvent {
    /**
     * No operation
     */
    data object Noop : SplashEvent()
}
