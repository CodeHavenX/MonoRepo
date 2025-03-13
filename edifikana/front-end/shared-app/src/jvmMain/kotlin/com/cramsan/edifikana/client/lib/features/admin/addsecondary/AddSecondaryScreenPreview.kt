package com.cramsan.edifikana.client.lib.features.admin.addsecondary

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.features.admin.addstaffsecondary.AddSecondaryContent
import com.cramsan.edifikana.client.lib.features.admin.addstaffsecondary.AddSecondaryStaffUIState
import com.cramsan.edifikana.client.ui.theme.AppTheme

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
