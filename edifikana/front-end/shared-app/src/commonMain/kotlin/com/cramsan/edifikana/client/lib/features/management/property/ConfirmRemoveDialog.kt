package com.cramsan.edifikana.client.lib.features.management.property

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.cramsan.framework.core.compose.Dialog
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A dialog that asks the user to confirm the removal of an item.
 *
 * @param onConfirmed Callback invoked when the user confirms the removal.
 */
class ConfirmRemoveDialog(
    private val onConfirmed: () -> Unit,
) : Dialog() {
    @Composable
    override fun Content() {
        AlertDialog(
            title = {
                Text(text = "Confirm Removal")
            },
            text = {
                Text(text = "Are you sure you want to remove this item?")
            },
            onDismissRequest = { hide() },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmed()
                        hide()
                    }
                ) {
                    Text("Confirm")
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
    ConfirmRemoveDialog(
        onConfirmed = {},
    ).Content()
}
