package com.cramsan.edifikana.client.lib.features.account

import androidx.navigation.NavBackStackEntry
import com.cramsan.framework.annotations.WebPath
import com.cramsan.framework.core.compose.navigation.WebDestination
import kotlinx.serialization.Serializable

/**
 * Destinations in the account nav graph.
 */
@Serializable
sealed class AccountDestination : WebDestination {
    /** My account overview screen destination. */
    @Serializable
    @WebPath("/account")
    data object MyAccountDestination : AccountDestination()

    /** Notification preferences screen destination. */
    @Serializable
    @WebPath("/account/notifications")
    data object NotificationsDestination : AccountDestination()

    /** Change password screen destination. */
    @Serializable
    @WebPath("/account/change-password")
    data object ChangePasswordDestination : AccountDestination()

    override fun toWebPath(): String = AccountDestinationWebRoutes.toWebPath(this)

    companion object {
        /** Parses [path] and returns the matching [AccountDestination], or null if unrecognised. */
        fun fromWebPath(path: String): AccountDestination? = AccountDestinationWebRoutes.fromWebPath(path)

        /** Converts [entry] to a canonical URL string, or null if it belongs to a different nav graph. */
        fun toWebPath(entry: NavBackStackEntry): String? = AccountDestinationWebRoutes.toWebPath(entry)
    }
}
