package com.cramsan.templatereplaceme.client.lib.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.templatereplaceme.client.lib.features.main.menu.MainMenuScreen
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowNavGraphDestination
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Main Nav Graph Route.
 */
fun NavGraphBuilder.mainNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = TemplateReplaceMeWindowNavGraphDestination.AuthNavGraphDestination::class,
        startDestination = MainDestination.MenuDestination,
        typeMap = typeMap,
    ) {
        composable(MainDestination.MenuDestination::class) {
            MainMenuScreen()
        }
    }
}
