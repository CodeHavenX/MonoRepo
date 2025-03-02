package com.cramsan.edifikana.client.lib.features.admin.addsecondary

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.lib.features.admin.addstaffsecondary.AddSecondaryContent
import com.cramsan.edifikana.client.lib.features.admin.addstaffsecondary.AddSecondaryStaffUIState

/**
 * Preview for the AddSecondary feature screen.
 */
@Preview
@Composable
private fun AddSecondaryScreenPreview() {
    AddSecondaryContent(
        content = AddSecondaryStaffUIState(true, null),
        onBackSelected = {},
        onSaveDataClicked = { _, _, _, _, _ -> },
    )
}
