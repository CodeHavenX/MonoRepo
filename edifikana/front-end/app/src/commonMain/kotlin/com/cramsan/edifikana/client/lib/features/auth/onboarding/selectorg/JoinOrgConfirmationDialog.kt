package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.cramsan.ui.preview.ComponentPreviews
import edifikana_lib.Res
import edifikana_lib.edifikana_string_cancel
import edifikana_lib.join_org_dialog_confirm_button
import edifikana_lib.join_org_dialog_message
import edifikana_lib.join_org_dialog_title
import org.jetbrains.compose.resources.stringResource

/**
 * Confirmation dialog content asking the user whether they want to accept an
 * organization invite.
 */
@Composable
fun JoinOrgConfirmationContent(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.join_org_dialog_title)) },
        text = { Text(stringResource(Res.string.join_org_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.join_org_dialog_confirm_button))
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
private fun JoinOrgConfirmationContentPreview() {
    JoinOrgConfirmationContent(
        onConfirm = {},
        onDismiss = {},
    )
}
