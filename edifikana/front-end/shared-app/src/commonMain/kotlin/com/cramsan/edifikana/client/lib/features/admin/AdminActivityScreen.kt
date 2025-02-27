package com.cramsan.edifikana.client.lib.features.admin

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.cramsan.edifikana.client.lib.features.admin.addproperty.AddPropertyScreen
import com.cramsan.edifikana.client.lib.features.admin.hub.HubScreen
import com.cramsan.edifikana.client.lib.features.admin.properties.PropertyManagerScreen
import com.cramsan.edifikana.client.lib.features.admin.property.PropertyScreen
import com.cramsan.framework.core.compose.RouteSafePath

/**
 * Admin Activity Route.
 */
@OptIn(RouteSafePath::class)
fun NavGraphBuilder.adminActivityNavigation(
    route: String,
) {
    navigation(
        route = route,
        startDestination = AdminActivityRoute.Hub.route,
    ) {
        AdminActivityRoute.entries.forEach {
            when (it) {
                AdminActivityRoute.Properties -> composable(it.route) {
                    PropertyManagerScreen()
                }
                AdminActivityRoute.Property -> composable(it.route) { backstackEntry ->
                    PropertyScreen(
                        AdminRouteDestination.PropertyAdminDestination.fromPath(backstackEntry)
                    )
                }
                AdminActivityRoute.AddProperty -> composable(it.route) {
                    AddPropertyScreen()
                }
                AdminActivityRoute.Hub -> composable(it.route) {
                    HubScreen()
                }
            }
        }
    }
}
