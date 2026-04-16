package com.cramsan.flyerboard.client.lib.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.flyerboard.client.lib.features.main.flyer_list.FlyerListScreen
import com.cramsan.flyerboard.client.lib.features.main.menu.MainMenuScreen
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowNavGraphDestination
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Main Nav Graph Route.
 */
fun NavGraphBuilder.mainNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = FlyerBoardWindowNavGraphDestination.MainNavGraphDestination::class,
        startDestination = MainDestination.FlyerListDestination,
        typeMap = typeMap,
    ) {
        composable(
            MainDestination.FlyerListDestination::class,
            typeMap = typeMap,
        ) {
            FlyerListScreen()
        }
        composable(
            MainDestination.MenuDestination::class,
            typeMap = typeMap,
        ) {
            MainMenuScreen()
        }
    }
}
