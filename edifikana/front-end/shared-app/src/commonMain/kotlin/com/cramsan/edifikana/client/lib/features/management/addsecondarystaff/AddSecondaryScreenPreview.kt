package com.cramsan.edifikana.client.lib.features.management.addsecondarystaff

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the AddSecondary feature screen.
 */
@Preview
@Composable
private fun AddSecondaryScreenPreview() = AppTheme {
    AddSecondaryContent(
        content = AddSecondaryStaffUIState(true, null),
        onBackSelected = {},
        onSaveDataClicked = { _, _, _, _, _ -> },
    )
}
