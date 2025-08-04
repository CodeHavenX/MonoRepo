package com.cramsan.framework.core.compose.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import kotlin.reflect.KClass

/**
 * Navigation graph builder for creating a navigation graph with a specified start destination
 * and graph destination type.
 */
fun <G : NavigationGraphDestination, D : Destination,> NavGraphBuilder.navigationGraph(
    graphDestination: KClass<G>,
    startDestination: D,
    builder: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = graphDestination,
        startDestination = startDestination,
        builder = builder,
    )
}
