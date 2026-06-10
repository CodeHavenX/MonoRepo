package com.cramsan.edifikana.client.lib.features.account

import androidx.navigation.NavBackStackEntry
import com.cramsan.framework.core.compose.navigation.WebDestination
import com.cramsan.framework.core.compose.navigation.toWebPathIfRoute
import com.cramsan.framework.core.compose.navigation.webRoute
import kotlinx.serialization.Serializable

/**
 * Destinations in the account nav graph.
 */
@Serializable
sealed class AccountDestination : WebDestination {
    /** My account overview screen destination. */
    @Serializable
    data object MyAccountDestination : AccountDestination()

    /** Notification preferences screen destination. */
    @Serializable
    data object NotificationsDestination : AccountDestination()

    /** Change password screen destination. */
    @Serializable
    data object ChangePasswordDestination : AccountDestination()

    override fun toWebPath(): String =
        when (this) {
            is MyAccountDestination -> Companion.myAccountRoute.toWebPath(this)
            is NotificationsDestination -> Companion.notificationsRoute.toWebPath(this)
            is ChangePasswordDestination -> Companion.changePasswordRoute.toWebPath(this)
        }

    companion object {
        private val myAccountRoute by lazy { webRoute<MyAccountDestination>("/account") }
        private val notificationsRoute by lazy { webRoute<NotificationsDestination>("/account/notifications") }
        private val changePasswordRoute by lazy { webRoute<ChangePasswordDestination>("/account/change-password") }

        /** Parses [path] and returns the matching [AccountDestination], or null if unrecognised. */
        fun fromWebPath(path: String): AccountDestination? =
            myAccountRoute.fromWebPath(path)
                ?: notificationsRoute.fromWebPath(path)
                ?: changePasswordRoute.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? =
            entry.toWebPathIfRoute<MyAccountDestination>()
                ?: entry.toWebPathIfRoute<NotificationsDestination>()
                ?: entry.toWebPathIfRoute<ChangePasswordDestination>()
    }
}
