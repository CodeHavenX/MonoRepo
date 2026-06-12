package com.cramsan.edifikana.client.lib.features.account.account

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered in the account.
 */
sealed class AccountEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : AccountEvent()
}
