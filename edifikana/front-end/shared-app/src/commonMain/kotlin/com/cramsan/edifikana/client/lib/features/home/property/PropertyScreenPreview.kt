package com.cramsan.edifikana.client.lib.features.home.property

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
            employee = listOf(
                EmployeeUIModel(null, "Manager 1", false),
                EmployeeUIModel(null, "Manager 2", true),
            ),
            addEmployeeError = false,
            addEmployeeEmail = "",
            suggestions = listOf()
        ),
        onBackSelected = {},
        onNewEmployeeSelected = { _ -> },
        onEmployeeActionSelected = { _ -> },
        onSaveChangesSelected = { },
        onNewSuggestionsRequested = { _ -> },
        onShowRemoveDialogSelected = { },
        onPropertyNameChanged = { },
        onAddressChanged = { },
    )
}
