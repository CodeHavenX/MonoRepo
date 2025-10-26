package com.cramsan.runasimi.client.lib.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.runasimi.client.lib.features.main.verbs.VerbsScreen
import com.cramsan.runasimi.client.lib.features.window.RunasimiNavGraphDestination
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Auth Nav Graph Route.
 */
fun NavGraphBuilder.mainNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = RunasimiNavGraphDestination.MainNavGraphDestination::class,
        startDestination = MainDestination.NavDestination,
        typeMap = typeMap,
    ) {
        composable(MainDestination.NavDestination::class) {
            VerbsScreen()
        }
    }
}
