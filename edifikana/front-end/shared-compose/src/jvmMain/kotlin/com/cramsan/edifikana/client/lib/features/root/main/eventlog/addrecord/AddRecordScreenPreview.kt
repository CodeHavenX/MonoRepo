package com.cramsan.edifikana.client.lib.features.root.main.eventlog.addrecord

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.lib.model.StaffId

/**
 * Preview for the AddRecord Screen.
 */
@Preview
@Composable
private fun AddRecordScreen() {
    AddRecord(
        listOf(
            AddRecordUIModel(
                "Juan Perez",
                StaffId("1"),
            ),
            AddRecordUIModel(
                "Maria Rodriguez",
                null,
            ),
        ),
        true,
    ) { _, _, _, _, _, _, _ -> }
}