package com.cramsan.edifikana.client.lib.features.admin.stafflist

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffStatus
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the StaffList feature screen.
 */
@Preview
@Composable
private fun StaffListScreenPreview() = AppTheme {
    StaffListContent(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        content = StaffListUIState(
            true,
            listOf(
                StaffUIModel(
                    id = StaffId("1"),
                    name = "John Doe",
                    email = "john@google.com",
                    status = StaffStatus.ACTIVE,
                ),
                StaffUIModel(
                    id = StaffId("2"),
                    name = "Jane Doe",
                    email = "jane@apple.com",
                    status = StaffStatus.PENDING,
                ),
                StaffUIModel(
                    id = StaffId("3"),
                    name = "Jack Doe",
                    email = "test@demo.com",
                    status = StaffStatus.PENDING,
                ),
            ),
        ),
        onAddPrimaryStaffSelected = {},
        onStaffSelected = {},
        onAddSecondaryStaffSelected = {},
    )
}
