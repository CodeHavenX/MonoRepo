package com.cramsan.templatereplaceme.client.lib.features.activityreplaceme

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import com.cramsan.framework.core.compose.navigation.navigationGraph
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowNavGraphDestination
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Nav graph builder extension for the [ActivityReplaceme] navigation graph.
 *
 * Register this in the root nav host and add [ActivityReplacemeNavGraphDestination] to the
 * application's nav graph destinations.
 */
fun NavGraphBuilder.activityreplacemeNavGraphNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
) {
    navigationGraph(
        graphDestination = TemplateReplaceMeWindowNavGraphDestination.ActivityReplacemeNavGraphDestination::class,
        startDestination = ActivityReplacemeDestination.FeatureReplacemeDestination,
        typeMap = typeMap,
    ) {
        // TODO: add composable entries for each ActivityReplacemeDestination screen
    }
}
