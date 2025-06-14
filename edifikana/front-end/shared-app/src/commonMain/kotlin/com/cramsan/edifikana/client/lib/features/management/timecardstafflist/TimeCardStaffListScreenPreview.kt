package com.cramsan.edifikana.client.lib.features.management.timecardstafflist

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.StaffId
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun TimeCardStaffListScreenPreview() = AppTheme {
    TimeCardStaffList(
        TimeCardStaffListUIState(
            isLoading = true,
            staffs = listOf(
                TimeCardStaffUIModel(
                    "Cesar Andres Ramirez Sanchez",
                    StaffId("John"),
                ),
                TimeCardStaffUIModel(
                    "Jane Smith",
                    StaffId("Jane"),
                ),
            ),
            title = ""
        ),
        onStaffClick = {},
        onCloseSelected = {},
    )
}
