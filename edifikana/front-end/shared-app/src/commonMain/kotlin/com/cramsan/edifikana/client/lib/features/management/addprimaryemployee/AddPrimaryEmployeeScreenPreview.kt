package com.cramsan.edifikana.client.lib.features.management.addprimaryemployee

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the AddPrimaryEmployee feature screen.
 */
@Preview
@Composable
private fun AddPrimaryEmployeeScreenPreview() = AppTheme {
    AddPrimaryEmployeeContent(
        content = AddPrimaryEmployeeUIState(
            isLoading = true,
            title = null,
            errorMessage = null,
        ),
        onBackSelected = {},
        onInviteSelected = {},
    )
}
