package com.cramsan.edifikana.client.lib.features.settings.organizations.transferownership

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the TransferOwnership feature.
 */
sealed class TransferOwnershipEvent : ViewModelEvent {
    /** No operation. */
    data object Noop : TransferOwnershipEvent()
}
