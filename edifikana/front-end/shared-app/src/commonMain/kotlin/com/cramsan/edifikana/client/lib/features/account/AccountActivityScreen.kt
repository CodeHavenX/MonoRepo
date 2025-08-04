package com.cramsan.edifikana.client.lib.features.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cramsan.edifikana.client.lib.features.account.account.AccountScreen
import com.cramsan.edifikana.client.lib.features.account.changepassword.ChangePasswordDialog
import com.cramsan.edifikana.client.lib.features.account.notifications.NotificationsScreen
import com.cramsan.edifikana.client.lib.features.window.ActivityRouteDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph

/**
 * Account Activity Route.
 */
fun NavGraphBuilder.accountActivityNavigation() {
    navigationGraph(
        graphDestination = ActivityRouteDestination.AccountRouteDestination::class,
        startDestination = AccountRouteDestination.AccountDestination,
    ) {
        composable(AccountRouteDestination.AccountDestination::class) {
            AccountScreen()
        }
        composable(AccountRouteDestination.NotificationsDestination::class) {
            NotificationsScreen()
        }
        dialog(AccountRouteDestination.ChangePasswordDestination) {
            ChangePasswordDialog()
        }
    }
}
