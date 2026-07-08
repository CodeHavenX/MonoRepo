package com.cramsan.edifikana.client.lib.features.auth.invitationaccept

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events emitted by [InvitationAcceptViewModel].
 *
 * Navigation events are dispatched via the window event bus as [com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent].
 */
sealed class InvitationAcceptEvent : ViewModelEvent {
    /** No-op event placeholder. */
    data object Noop : InvitationAcceptEvent()
}
