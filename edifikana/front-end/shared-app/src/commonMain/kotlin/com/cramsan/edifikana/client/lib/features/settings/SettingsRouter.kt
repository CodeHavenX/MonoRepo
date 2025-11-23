package com.cramsan.edifikana.client.lib.features.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Settings Nav Graph Route.
 */
fun NavGraphBuilder.settingsNavGraph(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
) {
    navigationGraph(
        graphDestination = EdifikanaNavGraphDestination.SettingsNavGraphDestination::class,
        startDestination = SettingsDestination.SettingsOverviewDestination,
        typeMap = typeMap,
    ) {
        composable(SettingsDestination.SettingsOverviewDestination::class) {
            SettingsScreen()
        }
    }
}
