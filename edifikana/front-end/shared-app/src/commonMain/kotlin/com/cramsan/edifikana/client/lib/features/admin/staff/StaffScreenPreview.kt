package com.cramsan.edifikana.client.lib.features.admin.staff

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Staff feature screen.
 */
@Preview
@Composable
private fun StaffScreenPreview() = AppTheme {
    StaffContent(
        content = StaffUIState(
            title = "StaffScreenPreview",
            isLoading = true,
        ),
        onBackSelected = {},
    )
}
