package com.cramsan.edifikana.client.lib.features.admin.property

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
            propertyName = "Property Name",
            address = "Address",
            isLoading = false,
            managers = listOf("Manager 1", "Manager 2"),
            addManagerError = false,
            addManagerEmail = "",
            suggestions = listOf()
        ),
        onBackSelected = {},
        onNewManagerSelected = { _ -> },
        onRemoveManagerSelected = { _ -> },
        onSaveChangesSelected = { _, _ -> },
        onSuggestionSelected = { },
        onNewSuggestionsRequested = { _ -> },
    )
}
