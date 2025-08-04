package com.cramsan.edifikana.client.lib.features.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.cramsan.edifikana.client.lib.features.account.account.AccountScreen
import com.cramsan.edifikana.client.lib.features.account.changepassword.ChangePasswordDialog
import com.cramsan.edifikana.client.lib.features.account.notifications.NotificationsScreen
import com.cramsan.edifikana.client.lib.features.window.ActivityRouteDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Account Activity Route.
 */
fun NavGraphBuilder.accountActivityNavigation(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
) {
    navigationGraph(
        graphDestination = ActivityRouteDestination.AccountRouteDestination::class,
        startDestination = AccountRouteDestination.AccountDestination,
        typeMap = typeMap,
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
