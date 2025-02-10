package com.cramsan.edifikana.client.lib.features.root.main.timecard.addstaff

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.features.main.timecard.addstaff.AddStaffForm

@Preview
@Composable
private fun AddStaffScreenPreview() {
    AddStaffForm(
        isLoading = true,
        onSaveDataClicked = { _, _, _, _, _ -> },
    )
}
