package com.cramsan.framework.core.compose.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navigation
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Navigation graph builder for creating a navigation graph with a specified start destination
 * and graph destination type.
 */
fun <G : NavigationGraphDestination, D : Destination,> NavGraphBuilder.navigationGraph(
    graphDestination: KClass<G>,
    startDestination: D,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
    builder: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = graphDestination,
        startDestination = startDestination,
        typeMap = typeMap,
        builder = builder,
    )
}
