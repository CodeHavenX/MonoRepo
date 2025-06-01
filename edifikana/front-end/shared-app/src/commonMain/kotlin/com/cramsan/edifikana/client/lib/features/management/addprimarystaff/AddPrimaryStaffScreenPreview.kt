package com.cramsan.edifikana.client.lib.features.management.addprimarystaff

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the AddPrimaryStaff feature screen.
 */
@Preview
@Composable
private fun AddPrimaryStaffScreenPreview() = AppTheme {
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
