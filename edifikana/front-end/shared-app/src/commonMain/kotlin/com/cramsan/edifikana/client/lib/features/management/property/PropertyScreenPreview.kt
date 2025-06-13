package com.cramsan.edifikana.client.lib.features.management.property

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Property feature screen.
 */
@Preview
@Composable
private fun PropertyScreenPreview() = AppTheme {
    PropertyContent(
        content = PropertyUIState(
            title = "Property Management",
            propertyName = "Property Name",
            address = "Address",
            isLoading = false,
            staff = listOf(
                StaffUIModel(null, "Manager 1", false),
                StaffUIModel(null, "Manager 2", true),
            ),
            addStaffError = false,
            addStaffEmail = "",
            suggestions = listOf()
        ),
        onBackSelected = {},
        onNewStaffSelected = { _ -> },
        onStaffActionSelected = { _ -> },
        onSaveChangesSelected = { },
        onNewSuggestionsRequested = { _ -> },
        onShowRemoveDialogSelected = { },
        onPropertyNameChanged = { },
        onAddressChanged = { },
    )
}
