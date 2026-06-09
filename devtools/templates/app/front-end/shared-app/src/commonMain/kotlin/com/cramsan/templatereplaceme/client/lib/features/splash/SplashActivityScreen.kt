package com.cramsan.templatereplaceme.client.lib.features.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowNavGraphDestination
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Splash Nav Graph Route.
 */
fun NavGraphBuilder.splashNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    initialDestination: Destination? = null,
) {
    navigationGraph(
        graphDestination = TemplateReplaceMeWindowNavGraphDestination.SplashNavGraphDestination::class,
        startDestination = SplashDestination,
        typeMap = typeMap,
    ) {
        composable(
            SplashDestination::class,
            typeMap = typeMap,
        ) {
            SplashScreen(initialDestination = initialDestination)
        }
    }
}
