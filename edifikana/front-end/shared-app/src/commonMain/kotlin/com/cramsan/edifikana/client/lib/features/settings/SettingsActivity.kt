package com.cramsan.edifikana.client.lib.features.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.edifikana.client.lib.features.settings.general.SettingsScreen
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Settings Nav Graph Activity.
 */
fun NavGraphBuilder.settingsNavGraphNavigation(typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap()) {
    navigationGraph(
        graphDestination = EdifikanaNavGraphDestination.SettingsNavGraphDestination::class,
        startDestination = SettingsDestination.GeneralSettingsDestination,
        typeMap = typeMap,
    ) {
        composable(SettingsDestination.GeneralSettingsDestination::class) {
            SettingsScreen()
        }
    }
}
