package com.cramsan.edifikana.client.lib.features.main.timecard.stafflist

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.StaffId

@Preview
@Composable
private fun StaffListScreenPreview() = AppTheme {
    StaffList(
        StaffListUIState(
            isLoading = true,
            staffs = listOf(
                StaffUIModel(
                    "Cesar Andres Ramirez Sanchez",
                    StaffId("John"),
                ),
                StaffUIModel(
                    "Jane Smith",
                    StaffId("Jane"),
                ),
            ),
            title = ""
        ),
        onStaffClick = {},
        onCloseSelected = {},
        onAddStaffSelected = {},
    )
}
