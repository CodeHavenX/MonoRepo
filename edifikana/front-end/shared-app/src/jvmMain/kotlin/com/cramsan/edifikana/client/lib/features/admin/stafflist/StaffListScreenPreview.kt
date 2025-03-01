package com.cramsan.edifikana.client.lib.features.admin.stafflist

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

/**
 * Preview for the StaffList feature screen.
 */
@Preview
@Composable
private fun StaffListScreenPreview() {
    StaffListContent(
        content = StaffListUIState(
            true,
            listOf("Staff 1", "Staff 2", "Staff 3"),
        ),
        onAddPrimaryStaffSelected = {},
        onStaffSelected = {},
        onAddSecondaryStaffSelected = {},
    )
}
