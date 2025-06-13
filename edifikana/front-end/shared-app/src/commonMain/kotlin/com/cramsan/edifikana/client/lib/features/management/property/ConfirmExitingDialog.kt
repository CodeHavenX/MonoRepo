package com.cramsan.edifikana.client.lib.features.management.property

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.cramsan.framework.core.compose.Dialog
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A dialog that asks the user to confirm if they want to save changes before exiting.
 *
 * @param onSaveSelected Callback invoked when the user chooses to save changes and exit.
 */
class ConfirmExitingDialog(
    private val onSaveSelected: () -> Unit,
) : Dialog() {
    @Composable
    override fun Content() {
        AlertDialog(
            title = {
                Text(text = "There are unsaved changes")
            },
            text = {
                Text(text = "Do you want to save changes before exiting?")
            },
            onDismissRequest = { hide() },
            confirmButton = {
                TextButton(
                    onClick = {
                        onSaveSelected()
                        hide()
                    }
                ) {
                    Text("Save and Exit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { hide() }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Preview
@Composable
private fun ConfirmRemoveDialogPreview() {
    ConfirmExitingDialog(
        onSaveSelected = {},
    ).Content()
}
