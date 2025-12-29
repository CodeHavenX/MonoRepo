package com.cramsan.edifikana.client.lib.features.home.employeeoverview

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
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
                orgId = OrganizationId("org-1"),
                employeeList = listOf(
                    EmployeeItemUIModel(
                        id = UserId("user-1"),
                        name = "John Doe",
                        email = "john.doe@example.com",
                        imageUrl = null,
                    ),
                    EmployeeItemUIModel(
                        id = UserId("user-2"),
                        name = "Jane Smith",
                        email = "jane.smith@example.com",
                        imageUrl = null,
                    ),
                ),
                inviteList = listOf(
                    InviteItemUIModel(
                        email = "pending@example.com",
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
                orgId = OrganizationId("org-1"),
                employeeList = emptyList(),
                inviteList = emptyList(),
            ),
        )
    }
}
