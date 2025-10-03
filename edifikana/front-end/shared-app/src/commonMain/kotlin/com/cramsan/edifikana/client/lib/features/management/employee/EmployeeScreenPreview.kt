package com.cramsan.edifikana.client.lib.features.management.employee

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Employee feature screen.
 */
@Preview
@Composable
private fun EmployeeScreenPreview_Primary_Employee() = AppTheme {
    EmployeeContent(
        content = EmployeeUIState(
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
            employeeId = null,
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
private fun EmployeeScreenPreview_Secondary_Employee() = AppTheme {
    EmployeeContent(
        content = EmployeeUIState(
            title = "Cesar Vargas",
            isLoading = true,
            idType = IdType.DNI,
            idNNumber = "12345678",
            firstName = "Cesar",
            lastName = "Vargas",
            email = "admin@cenit.com",
            role = EmployeeRole.SECURITY_COVER,
            isEditable = false,
            canSave = null,
            employeeId = null,
        ),
        onBackSelected = {},
        onSaveClicked = {},
        onFirstNameChange = {},
        onLastNameChange = {},
        onRoleSelected = {},
    )
}
