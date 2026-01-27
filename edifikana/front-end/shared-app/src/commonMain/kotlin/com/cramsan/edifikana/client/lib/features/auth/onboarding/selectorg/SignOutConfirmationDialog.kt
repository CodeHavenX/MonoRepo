package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.cramsan.framework.core.compose.Dialog
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A confirmation dialog that asks the user if they want to sign out
 * and leave the onboarding process.
 */
class SignOutConfirmationDialog(private val onConfirm: () -> Unit, private val onDismiss: () -> Unit) : Dialog() {

    @Composable
    override fun Content() {
        AlertDialog(
            onDismissRequest = {
                hide()
                onDismiss()
            },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out and leave the onboarding process?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        hide()
                        onConfirm()
                    },
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        hide()
                        onDismiss()
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Preview
@Composable
private fun SignOutConfirmationDialogPreview() {
    SignOutConfirmationDialog(
        onConfirm = {},
        onDismiss = {},
    ).Content()
}
