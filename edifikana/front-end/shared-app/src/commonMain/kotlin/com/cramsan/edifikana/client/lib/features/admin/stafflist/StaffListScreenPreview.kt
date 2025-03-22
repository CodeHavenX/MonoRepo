package com.cramsan.edifikana.client.lib.features.admin.stafflist

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the StaffList feature screen.
 */
@Preview
@Composable
private fun StaffListScreenPreview() = AppTheme {
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
