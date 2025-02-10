package com.cramsan.edifikana.client.lib.features.root.main.timecard.stafflist

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.features.main.timecard.stafflist.StaffList
import com.cramsan.edifikana.client.lib.features.main.timecard.stafflist.StaffUIModel
import com.cramsan.edifikana.lib.model.StaffId

@Preview
@Composable
private fun StaffListScreenPreview() {
    StaffList(
        isLoading = true,
        staffs = listOf(
            StaffUIModel(
                "Cesar Andres Ramirez Sanchez",
                StaffId("John"),
            ),
            StaffUIModel(
                "2",
                StaffId("Jane"),
            ),
        ),
        onStaffClick = {},
        onAddStaffClick = {},
    )
}
