package com.cramsan.flyerboard.client.lib.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.flyerboard.client.lib.features.main.archive.ArchiveScreen
import com.cramsan.flyerboard.client.lib.features.main.flyer_detail.FlyerDetailScreen
import com.cramsan.flyerboard.client.lib.features.main.flyer_list.FlyerListScreen
import com.cramsan.flyerboard.client.lib.features.main.menu.MainMenuScreen
import com.cramsan.flyerboard.client.lib.features.main.moderation_queue.ModerationQueueScreen
import com.cramsan.flyerboard.client.lib.features.main.my_flyers.MyFlyersScreen
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
            MainDestination.FlyerDetailDestination::class,
            typeMap = typeMap,
        ) {
            FlyerDetailScreen(destination = it.toRoute())
        }
        composable(
            MainDestination.MyFlyersDestination::class,
            typeMap = typeMap,
        ) {
            MyFlyersScreen()
        }
        composable(
            MainDestination.ArchiveDestination::class,
            typeMap = typeMap,
        ) {
            ArchiveScreen()
        }
        composable(
            MainDestination.ModerationQueueDestination::class,
            typeMap = typeMap,
        ) {
            ModerationQueueScreen()
        }
        composable(
            MainDestination.MenuDestination::class,
            typeMap = typeMap,
        ) {
            MainMenuScreen()
        }
    }
}
