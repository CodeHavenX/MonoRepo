package com.cramsan.edifikana.client.lib.features.account.notifications

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Notifications feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class NotificationsUIState(
    val title: String?,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = NotificationsUIState(
            title = null,
            isLoading = true,
        )
    }
}
