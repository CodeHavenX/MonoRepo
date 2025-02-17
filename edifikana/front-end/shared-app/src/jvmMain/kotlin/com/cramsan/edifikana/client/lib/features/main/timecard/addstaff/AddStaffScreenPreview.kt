package com.cramsan.edifikana.client.lib.features.main.timecard.addstaff

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview
@Composable
private fun AddStaffScreenPreview() {
    AddStaffForm(
        uiState = AddStaffUIState(
            isLoading = true,
            title = ""
        ),
        onSaveDataClicked = { _, _, _, _, _ -> },
        onBackSelected = { }
    )
}
