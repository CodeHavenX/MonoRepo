package com.cramsan.edifikana.client.lib.features.auth.setnewpassword

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events emitted by [SetNewPasswordViewModel].
 */
sealed class SetNewPasswordEvent : ViewModelEvent {
    /** No-op event. */
    data object Noop : SetNewPasswordEvent()
}
