package com.cramsan.architecture.client.features.debugsettings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.framework.core.compose.navigation.Destination
import com.cramsan.framework.core.compose.navigation.NavigationGraphDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Navigation destinations within the debug settings sub-graph.
 */
@Serializable
sealed class DebugSettingsDestination : Destination {
    /**
     * The main list of all registered setting groups.
     */
    @Serializable
    data object SettingsListDestination : DebugSettingsDestination()
}

/**
 * Registers the debug settings navigation sub-graph within a [NavGraphBuilder].
 *
 * The caller supplies the app-specific [NavigationGraphDestination] subtype as [graphDestination]
 * so the sub-graph can be embedded into any app's navigation hierarchy without coupling this
 * library to a specific app.
 *
 * Example usage:
 * ```kotlin
 * if (isDebugBuild) {
 *     debugSettingsNavGraph(
 *         graphDestination = MyAppNavGraphDestination.DebugSettingsNavGraphDestination::class,
 *         onBack = { navController.popBackStack() },
 *     )
 * }
 * ```
 *
 * @param G The app-specific [NavigationGraphDestination] type acting as the graph root.
 * @param graphDestination The runtime [KClass] of [G].
 * @param onBack Callback invoked when the user requests back navigation from the settings screen.
 * @param typeMap Optional custom [NavType] map for any additional type parameters.
 */
fun <G : NavigationGraphDestination> NavGraphBuilder.debugSettingsNavGraph(
    graphDestination: KClass<G>,
    onBack: () -> Unit,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = graphDestination,
        startDestination = DebugSettingsDestination.SettingsListDestination,
        typeMap = typeMap,
    ) {
        composable(
            DebugSettingsDestination.SettingsListDestination::class,
            typeMap = typeMap,
        ) {
            DebugSettingsScreen(onBack = onBack)
        }
    }
}
