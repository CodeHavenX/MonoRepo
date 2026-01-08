package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events for the SelectOrg screen.
 */
sealed class SelectOrgEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : SelectOrgEvent()

    /**
     * Show sign out confirmation dialog.
     */
    data object ShowSignOutConfirmation : SelectOrgEvent()
}
