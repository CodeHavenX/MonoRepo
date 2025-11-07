package com.cramsan.edifikana.client.lib.features.home.employeelist

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.UserId
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the EmployeeList feature screen.
 */
@Preview
@Composable
private fun EmployeeListScreenPreview() = AppTheme {
    EmployeeListContent(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        content = EmployeeListUIState(
            true,
            employeeList = listOf(
                UserUIModel(
                    userId = UserId("1"),
                    name = "John Doe",
                    email = "john@google.com",
                ),
                InviteUIModel(
                    inviteId = InviteId("2"),
                    email = "jane@apple.com",
                ),
                EmployeeMemberUIModel(
                    employeeId = EmployeeId("3"),
                    name = "Jack Doe",
                    email = "test@demo.com",
                ),
            ),
            activeOrgId = null,
        ),
        onAddPrimaryEmployeeSelected = {},
        onEmployeeSelected = {},
        onAddSecondaryEmployeeSelected = {},
        onUserSelected = {},
        onInviteSelected = {},
    )
}
