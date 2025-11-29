package com.cramsan.edifikana.client.lib.features.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cramsan.edifikana.client.lib.features.home.addproperty.AddPropertyScreen
import com.cramsan.edifikana.client.lib.features.home.drawer.DrawerScreen
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Management nav graph Route.
 */
@Suppress("LongMethod")
fun NavGraphBuilder.homeNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = EdifikanaNavGraphDestination.HomeNavGraphDestination::class,
        startDestination = HomeDestination.ManagementHub,
        typeMap = typeMap,
    ) {
        composable(
            HomeDestination.AddPropertyManagementDestination::class,
            typeMap = typeMap,
        ) {
            AddPropertyScreen(
                destination = it.toRoute(),
            )
        }
        composable(
            HomeDestination.ManagementHub::class,
            typeMap = typeMap,
        ) {
            DrawerScreen()
        }
    }
}
