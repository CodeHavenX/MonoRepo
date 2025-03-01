package com.cramsan.edifikana.client.lib.features.admin.staff

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

/**
 * Preview for the Staff feature screen.
 */
@Preview
@Composable
private fun StaffScreenPreview() {
    StaffContent(
        content = StaffUIState(
            title = "StaffScreenPreview",
            isLoading = true,
        ),
        onBackSelected = {},
    )
}
