package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.cramsan.framework.core.compose.Dialog
import edifikana_lib.Res
import edifikana_lib.join_org_dialog_confirm_button
import edifikana_lib.join_org_dialog_message
import edifikana_lib.join_org_dialog_title
import edifikana_lib.string_cancel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A confirmation dialog that asks the user if they want to accept an
 * organization invite.
 */
class JoinOrgConfirmationDialog(
    private val onConfirm: () -> Unit,
    private val onDismiss: () -> Unit,
) : Dialog() {

    @Composable
    override fun Content() {
        AlertDialog(
            onDismissRequest = {
                hide()
                onDismiss()
            },
            title = { Text(stringResource(Res.string.join_org_dialog_title)) },
            text = { Text(stringResource(Res.string.join_org_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        hide()
                        onConfirm()
                    }
                ) {
                    Text(stringResource(Res.string.join_org_dialog_confirm_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        hide()
                        onDismiss()
                    }
                ) {
                    Text(stringResource(Res.string.string_cancel))
                }
            }
        )
    }
}

@Preview
@Composable
private fun JoinOrgConfirmationDialogPreview() {
    JoinOrgConfirmationDialog(
        onConfirm = {},
        onDismiss = {}
    ).Content()
}

@Preview(locale = "es")
@Composable
private fun JoinOrgConfirmationDialogPreview_ES() {
    JoinOrgConfirmationDialog(
        onConfirm = {},
        onDismiss = {}
    ).Content()
}
