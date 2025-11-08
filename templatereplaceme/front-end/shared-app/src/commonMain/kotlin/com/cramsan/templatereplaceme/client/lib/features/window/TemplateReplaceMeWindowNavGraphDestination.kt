@file:Suppress("TooManyFunctions")

package com.cramsan.templatereplaceme.client.lib.features.window

import com.cramsan.framework.core.compose.navigation.NavigationGraphDestination
import kotlinx.serialization.Serializable

/**
 * Navigation graph destinations for the TemplateReplaceMe application.
 */
@Serializable
sealed class TemplateReplaceMeWindowNavGraphDestination : NavigationGraphDestination {

    /**
     * Splash navigation graph destination.
     */
    @Serializable
    data object SplashNavGraphDestination : TemplateReplaceMeWindowNavGraphDestination()

    /**
     * Auth navigation graph destination.
     */
    @Serializable
    data object AuthNavGraphDestination : TemplateReplaceMeWindowNavGraphDestination()
}
