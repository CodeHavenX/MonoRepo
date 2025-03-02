package com.cramsan.edifikana.client.lib.features.admin.addprimarystaff

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

/**
 * Preview for the AddPrimaryStaff feature screen.
 */
@Preview
@Composable
private fun AddPrimaryStaffScreenPreview() {
    AddPrimaryStaffContent(
        content = AddPrimaryStaffUIState(
            isLoading = true,
            title = null,
            errorMessage = null,
        ),
        onBackSelected = {},
        onInviteSelected = {},
    )
}
