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
        startDestination = AdminRoute.Hub.route,
    ) {
        AdminRoute.entries.forEach {
            when (it) {
                AdminRoute.Properties -> composable(it.route) {
                    PropertyManagerScreen()
                }
                AdminRoute.Property -> composable(it.route) { backstackEntry ->
                    PropertyScreen(
                        AdminDestination.PropertyAdminDestination.unpack(backstackEntry)
                    )
                }
                AdminRoute.AddProperty -> composable(it.route) {
                    AddPropertyScreen()
                }
                AdminRoute.Hub -> composable(it.route) {
                    HubScreen()
                }
            }
        }
    }
}
