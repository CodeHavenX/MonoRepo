package com.cramsan.edifikana.client.lib.features.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.cramsan.edifikana.client.lib.features.account.account.AccountScreen
import com.cramsan.edifikana.client.lib.features.account.changepassword.ChangePasswordDialog
import com.cramsan.edifikana.client.lib.features.account.notifications.NotificationsScreen
import com.cramsan.framework.annotations.RouteSafePath

/**
 * Account Activity Route.
 */
@OptIn(RouteSafePath::class)
fun NavGraphBuilder.accountActivityNavigation(
    route: String,
) {
    navigation(
        route = route,
        startDestination = AccountActivityRoute.Account.route,
    ) {
        AccountActivityRoute.entries.forEach {
            when (it) {
                AccountActivityRoute.Account -> composable(it.route) {
                    AccountScreen()
                }
                AccountActivityRoute.Notifications -> composable(it.route) {
                    NotificationsScreen()
                }
                AccountActivityRoute.ChangePassword -> dialog(it.route) {
                    ChangePasswordDialog()
                }
            }
        }
    }
}
