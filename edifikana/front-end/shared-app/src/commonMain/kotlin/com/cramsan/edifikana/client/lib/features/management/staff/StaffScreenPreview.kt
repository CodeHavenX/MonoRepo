package com.cramsan.edifikana.client.lib.features.management.staff

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Staff feature screen.
 */
@Preview
@Composable
private fun StaffScreenPreview_Primary_Staff() = AppTheme {
    StaffContent(
        content = StaffUIState(
            title = "Cesar Vargas",
            isLoading = true,
            idType = null,
            idNNumber = null,
            firstName = "Cesar",
            lastName = "Vargas",
            email = null,
            role = null,
            isEditable = true,
            canSave = true,
            staffId = null,
        ),
        onBackSelected = {},
        onSaveClicked = {},
        onFirstNameChange = {},
        onLastNameChange = {},
        onRoleSelected = {},
    )
}

@Preview
@Composable
private fun StaffScreenPreview_Secondary_Staff() = AppTheme {
    StaffContent(
        content = StaffUIState(
            title = "Cesar Vargas",
            isLoading = true,
            idType = IdType.DNI,
            idNNumber = "12345678",
            firstName = "Cesar",
            lastName = "Vargas",
            email = "admin@cenit.com",
            role = StaffRole.SECURITY_COVER,
            isEditable = false,
            canSave = null,
            staffId = null,
        ),
        onBackSelected = {},
        onSaveClicked = {},
        onFirstNameChange = {},
        onLastNameChange = {},
        onRoleSelected = {},
    )
}
