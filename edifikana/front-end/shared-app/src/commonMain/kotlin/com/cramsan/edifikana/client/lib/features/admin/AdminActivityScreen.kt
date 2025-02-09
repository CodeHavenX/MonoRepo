package com.cramsan.edifikana.client.lib.features.admin

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cramsan.edifikana.client.lib.features.RouteSafePath
import com.cramsan.edifikana.client.lib.features.admin.properties.PropertyManagerScreen
import com.cramsan.edifikana.client.lib.features.admin.property.PropertyScreen

/**
 * Admin Activity Route.
 */
@OptIn(RouteSafePath::class)
fun NavGraphBuilder.adminActivityNavigation(
    route: String,
) {
    navigation(
        route = route,
        startDestination = AdminActivityRoute.Properties.route,
    ) {
        AdminActivityRoute.entries.forEach {
            when (it) {
                AdminActivityRoute.Properties -> composable(it.route) {
                    PropertyManagerScreen()
                }
                AdminActivityRoute.Property -> composable(it.route) {
                    PropertyScreen()
                }
            }
        }
    }
}
