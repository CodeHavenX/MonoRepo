package com.cramsan.flyerboard.client.lib.features.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.cramsan.flyerboard.client.lib.features.main.archive.ArchiveScreen
import com.cramsan.flyerboard.client.lib.features.main.flyer_detail.FlyerDetailScreen
import com.cramsan.flyerboard.client.lib.features.main.flyer_edit.FlyerEditScreen
import com.cramsan.flyerboard.client.lib.features.main.flyer_list.FlyerListScreen
import com.cramsan.flyerboard.client.lib.features.main.flyer_submit.FlyerSubmitScreen
import com.cramsan.flyerboard.client.lib.features.main.moderation_queue.ModerationQueueScreen
import com.cramsan.flyerboard.client.lib.features.main.my_flyers.MyFlyersScreen
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowNavGraphDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Main Nav Graph Route.
 */
@Suppress("LongMethod")
fun NavGraphBuilder.mainNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    isAuthenticated: Boolean = false,
    onSignIn: () -> Unit = {},
    onSignOut: () -> Unit = {},
) {
    navigationGraph(
        graphDestination = FlyerBoardWindowNavGraphDestination.MainNavGraphDestination::class,
        startDestination = MainDestination.FlyerListDestination,
        typeMap = typeMap,
    ) {
        composable(
            MainDestination.FlyerListDestination::class,
            typeMap = typeMap,
            deepLinks = listOf(navDeepLink<MainDestination.FlyerListDestination>(basePath = BASE_URL)),
        ) {
            FlyerListScreen(
                isAuthenticated = isAuthenticated,
            )
        }
        composable(
            MainDestination.FlyerDetailDestination::class,
            typeMap = typeMap,
            deepLinks = listOf(navDeepLink<MainDestination.FlyerDetailDestination>(basePath = "$BASE_URL/flyer")),
        ) {
            FlyerDetailScreen(destination = it.toRoute())
        }
        composable(
            MainDestination.MyFlyersDestination::class,
            typeMap = typeMap,
            deepLinks = listOf(navDeepLink<MainDestination.MyFlyersDestination>(basePath = "$BASE_URL/my-flyers")),
        ) {
            MyFlyersScreen(
                isAuthenticated = isAuthenticated,
                onSignOut = onSignOut,
            )
        }
        composable(
            MainDestination.ArchiveDestination::class,
            typeMap = typeMap,
            deepLinks = listOf(navDeepLink<MainDestination.ArchiveDestination>(basePath = "$BASE_URL/archive")),
        ) {
            ArchiveScreen(
                isAuthenticated = isAuthenticated,
                onSignIn = onSignIn,
                onSignOut = onSignOut,
            )
        }
        composable(
            MainDestination.ModerationQueueDestination::class,
            typeMap = typeMap,
            deepLinks =
            listOf(
                navDeepLink<MainDestination.ModerationQueueDestination>(basePath = "$BASE_URL/moderation"),
            ),
        ) {
            ModerationQueueScreen(
                isAuthenticated = isAuthenticated,
                onSignOut = onSignOut,
            )
        }
        composable(
            MainDestination.FlyerEditDestination::class,
            typeMap = typeMap,
            deepLinks =
            listOf(
                navDeepLink<MainDestination.FlyerEditDestination>(basePath = "$BASE_URL/my-flyers/edit"),
            ),
        ) {
            FlyerEditScreen(destination = it.toRoute())
        }
        composable(
            MainDestination.FlyerSubmitDestination::class,
            typeMap = typeMap,
            deepLinks =
            listOf(
                navDeepLink<MainDestination.FlyerSubmitDestination>(basePath = "$BASE_URL/my-flyers/submit"),
            ),
        ) {
            FlyerSubmitScreen()
        }
    }
}

private const val BASE_URL = "https://flyerboard.com"
