package com.cramsan.edifikana.client.lib.features.account

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.cramsan.edifikana.client.lib.features.account.account.AccountScreen
import com.cramsan.edifikana.client.lib.features.account.changepassword.ChangePasswordDialog
import com.cramsan.edifikana.client.lib.features.account.notifications.NotificationsScreen
import com.cramsan.edifikana.client.lib.features.window.EdifikanaNavGraphDestination
import com.cramsan.framework.core.compose.navigation.navigationGraph
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * Account Nav Graph Route.
 */
fun NavGraphBuilder.accountNavGraph(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
) {
    navigationGraph(
        graphDestination = EdifikanaNavGraphDestination.AccountNavGraphDestination::class,
        startDestination = AccountDestination.MyAccountDestination,
        typeMap = typeMap,
    ) {
        composable(AccountDestination.MyAccountDestination::class) {
            AccountScreen()
        }
        composable(AccountDestination.NotificationsDestination::class) {
            NotificationsScreen()
        }
        dialog(AccountDestination.ChangePasswordDestination::class) {
            ChangePasswordDialog()
        }
    }
}
