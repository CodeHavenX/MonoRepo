package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.cramsan.framework.core.compose.Dialog
import edifikana_lib.Res
import edifikana_lib.edifikana_string_cancel
import edifikana_lib.edifikana_string_sign_out
import edifikana_lib.sign_out_onboarding_dialog_message
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A confirmation dialog that asks the user if they want to sign out
 * and leave the onboarding process.
 */
class SignOutConfirmationDialog(
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
            title = { Text(stringResource(Res.string.edifikana_string_sign_out)) },
            text = { Text(stringResource(Res.string.edifikana_string_sign_out_onboarding_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        hide()
                        onConfirm()
                    }
                ) {
                    Text(stringResource(Res.string.edifikana_string_sign_out))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        hide()
                        onDismiss()
                    }
                ) {
                    Text(stringResource(Res.string.edifikana_string_cancel))
                }
            }
        )
    }
}

@Preview
@Composable
private fun SignOutConfirmationDialogPreview() {
    SignOutConfirmationDialog(
        onConfirm = {},
        onDismiss = {}
    ).Content()
}

@Preview(locale = "es")
@Composable
private fun SignOutConfirmationDialogPreview_ES() {
    SignOutConfirmationDialog(
        onConfirm = {},
        onDismiss = {}
    ).Content()
}
