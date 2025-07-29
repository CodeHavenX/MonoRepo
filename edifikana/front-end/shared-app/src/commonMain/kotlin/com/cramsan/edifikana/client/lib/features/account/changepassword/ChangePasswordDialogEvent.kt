package com.cramsan.edifikana.client.lib.features.account.changepassword

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Represents events that can occur in the Change Password dialog.
 * This sealed class is used to define specific events that can be handled by the ViewModel.
 */
sealed class ChangePasswordDialogEvent : ViewModelEvent {

    /**
     * Event triggered when the user submits the change password form.
     */
    data object Noop : ChangePasswordDialogEvent()
}
