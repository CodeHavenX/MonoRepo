package com.cramsan.edifikana.client.lib.features.home.employeeoverview

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.EmployeeId
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the EmployeeOverview feature screen.
 */
@Preview
@Composable
private fun EmployeeOverviewScreenPreview() {
    AppTheme {
        EmployeeOverviewContent(
            content = EmployeeOverviewUIState(
                isLoading = false,
                employeeList = listOf(
                    EmployeeItemUIModel(
                        id = EmployeeId("employee-1"),
                        name = "John Doe",
                        role = "Property Manager",
                        imageUrl = null,
                    ),
                    EmployeeItemUIModel(
                        id = EmployeeId("employee-2"),
                        name = "Jane Smith",
                        role = "Maintenance Staff",
                        imageUrl = null,
                    ),
                ),
            ),
        )
    }
}

/**
 * Preview for the EmployeeOverview feature screen in empty state.
 */
@Preview
@Composable
private fun EmployeeOverviewScreenEmptyPreview() {
    AppTheme {
        EmployeeOverviewContent(
            content = EmployeeOverviewUIState(
                isLoading = false,
                employeeList = emptyList(),
            ),
        )
    }
}
