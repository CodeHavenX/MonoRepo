package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ComponentPreviews
import edifikana_lib.Res
import edifikana_lib.edifikana_string_cancel
import edifikana_lib.edifikana_string_sign_out
import edifikana_lib.sign_out_onboarding_dialog_message
import org.jetbrains.compose.resources.stringResource

/**
 * Confirmation dialog content asking the user whether they want to sign out
 * and leave the onboarding process.
 */
@Composable
fun SignOutConfirmationContent(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.edifikana_string_sign_out)) },
        text = { Text(stringResource(Res.string.sign_out_onboarding_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.edifikana_string_sign_out))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.edifikana_string_cancel))
            }
        },
    )
}

@ComponentPreviews
@Composable
private fun SignOutConfirmationContentPreview() {
    SignOutConfirmationContent(
        onConfirm = {},
        onDismiss = {},
    )
}
