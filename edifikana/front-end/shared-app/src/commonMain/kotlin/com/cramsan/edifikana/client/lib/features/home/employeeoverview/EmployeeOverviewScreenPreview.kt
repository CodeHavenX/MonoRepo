package com.cramsan.edifikana.client.lib.features.home.employeeoverview

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the EmployeeOverview feature screen.
 */
@ScreenPreviews
@Composable
private fun EmployeeOverviewScreenPreview() {
    AppTheme {
        EmployeeOverviewContent(
            content =
            EmployeeOverviewUIState(
                isLoading = false,
                orgId = OrganizationId("org-1"),
                employeeList =
                listOf(
                    UserItemUIModel(
                        id = UserId("user-1"),
                        name = "John Doe",
                        email = "john.doe@example.com",
                        imageUrl = null,
                    ),
                    UserItemUIModel(
                        id = UserId("user-2"),
                        name = "Jane Smith",
                        email = "jane.smith@example.com",
                        imageUrl = null,
                    ),
                    InviteItemUIModel(
                        email = "another.user@gmail.com",
                    ),
                ),
            ),
        )
    }
}

@ScreenPreviews
@Composable
private fun EmployeeOverviewScreenPreview_ES() {
    AppTheme {
        EmployeeOverviewContent(
            content =
            EmployeeOverviewUIState(
                isLoading = false,
                orgId = OrganizationId("org-1"),
                employeeList =
                listOf(
                    UserItemUIModel(
                        id = UserId("user-1"),
                        name = "Juan García",
                        email = "juan.garcia@example.com",
                        imageUrl = null,
                    ),
                    UserItemUIModel(
                        id = UserId("user-2"),
                        name = "María López",
                        email = "maria.lopez@example.com",
                        imageUrl = null,
                    ),
                    InviteItemUIModel(
                        email = "otro.usuario@gmail.com",
                    ),
                ),
            ),
        )
    }
}
