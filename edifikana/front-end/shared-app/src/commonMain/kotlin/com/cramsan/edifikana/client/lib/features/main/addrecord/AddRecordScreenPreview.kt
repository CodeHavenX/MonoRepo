package com.cramsan.edifikana.client.lib.features.main.addrecord

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.StaffId
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the AddRecord Screen.
 */
@Preview
@Composable
private fun AddRecordScreen() = AppTheme(debugLayoutInspection = true) {
    AddRecord(
        AddRecordUIState(
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
            false,
            ""
        ),
        onBackSelected = { },
    ) { _, _, _, _, _, _, _ -> }
}
