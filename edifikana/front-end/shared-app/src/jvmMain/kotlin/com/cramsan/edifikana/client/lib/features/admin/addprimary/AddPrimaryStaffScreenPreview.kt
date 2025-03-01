package com.cramsan.edifikana.client.lib.features.admin.addprimary

import androidx.compose.runtime.Composable
import androidx.compose.desktop.ui.tooling.preview.Preview

/**
 * Preview for the AddPrimaryStaff feature screen.
 */
@Preview
@Composable
private fun AddPrimaryStaffScreenPreview() {
    AddPrimaryStaffContent(
        content = AddPrimaryStaffUIState(true),
    )
}