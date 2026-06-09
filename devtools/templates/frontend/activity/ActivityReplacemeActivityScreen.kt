package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowNavGraphDestination
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * NavGraph builder extension for the [ActivityReplaceme] navigation graph.
 *
 * Call this function from the root NavHost (typically `WindowNavigationHost`) to
 * register all screens that belong to this activity:
 * ```
 * // Inside your NavHost composable:
 * activityReplacemeNavGraphNavigation(typeMap)
 * ```
 *
 * For each screen inside this activity:
 * 1. Add a `@Serializable data object MyDestination : ActivityReplacemeDestination()` in
 *    [ActivityReplacemeDestination].
 * 2. Add a `composable(ActivityReplacemeDestination.MyDestination::class) { MyScreen() }`
 *    entry inside the `navigationGraph { }` block below.
 *
 * Example with two screens and one dialog:
 * ```
 * composable(ActivityReplacemeDestination.ListDestination::class) { ListScreen() }
 * composable(ActivityReplacemeDestination.DetailDestination::class) { DetailScreen() }
 * dialog(ActivityReplacemeDestination.ConfirmDeleteDestination::class) { ConfirmDeleteDialog() }
 * ```
 *
 * If any destination carries arguments, pass a populated [typeMap]:
 * ```
 * val typeMap = mapOf(typeOf<MyId>() to MyIdNavType)
 * activityReplacemeNavGraphNavigation(typeMap)
 * ```
 *
 * ⚠️ `composable`/`dialog` do **not** take a `typeMap` argument of their own — `typeMap` is only
 * passed once, here, to [navigationGraph].
 *
 * ⚠️ `import androidx.navigation.compose.composable` is intentionally not pre-added: an unused
 * import would be auto-removed by `NoUnusedImports`. Add it yourself once the first
 * `composable(...)` entry is added below.
 *
 * TODO: Add `composable` / `dialog` entries for each [ActivityReplacemeDestination] screen.
 */
fun NavGraphBuilder.activityReplacemeNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = TemplateReplaceMeWindowNavGraphDestination.ActivityReplacemeNavGraphDestination::class,
        startDestination = ActivityReplacemeDestination.FeatureReplacemeDestination,
        typeMap = typeMap,
    ) {
        // TODO: Add composable entries for each ActivityReplacemeDestination screen, e.g.:
        //   composable(ActivityReplacemeDestination.FeatureReplacemeDestination::class) {
        //       FeatureReplacemeScreen()
        //   }
    }
}
