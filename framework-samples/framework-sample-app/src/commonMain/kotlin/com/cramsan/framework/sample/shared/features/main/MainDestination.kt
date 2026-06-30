@file:Suppress("TooManyFunctions")

package com.cramsan.framework.sample.shared.features.main

import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.core.compose.navigation.NavResultKey
import com.cramsan.framework.sample.shared.features.main.welcome.ThemeSelection
import kotlinx.serialization.Serializable

/**
 * Destinations in the main nav graph.
 */
sealed class MainDestination : Destination {
    /**
     * A class representing a main menu.
     */
    @Serializable
    data object MainMenuDestination : MainDestination()

    /**
     * A class representing a halt util.
     */
    @Serializable
    data object HaltUtilDestination : MainDestination()

    /**
     * A class representing a logging destination.
     */
    @Serializable
    data object LoggingDestination : MainDestination()

    /**
     * A class representing a preferences destination.
     */
    @Serializable
    data object PreferencesDestination : MainDestination()

    /**
     * A class representing a thread util destination.
     */
    @Serializable
    data object ThreadUtilDestination : MainDestination()

    /**
     * A class representing an assert util destination.
     */
    @Serializable
    data object AssertUtilDestination : MainDestination()

    /**
     * A class representing a metrics destination.
     */
    @Serializable
    data object MetricsDestination : MainDestination()

    /**
     * A class representing a configuration destination.
     */
    @Serializable
    data object ConfigurationDestination : MainDestination()

    /**
     * A class representing a crash handler destination.
     */
    @Serializable
    data object CrashHandlerDestination : MainDestination()

    /**
     * A class representing a user events destination.
     */
    @Serializable
    data object UserEventsDestination : MainDestination()

    /**
     * A class representing a remote config destination.
     */
    @Serializable
    data object RemoteConfigDestination : MainDestination()

    /**
     * A class representing a dispatcher provider destination.
     */
    @Serializable
    data object DispatcherDestination : MainDestination()

    /**
     * A dialog destination that asks the user to pick a theme and returns the selection
     * to the caller via [themeResult].
     */
    @Serializable
    data object WelcomeDialogDestination : MainDestination() {
        val themeResult = NavResultKey<ThemeSelection>("welcome_theme_result")
    }
}
