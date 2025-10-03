package com.cramsan.edifikana.client.lib.features.management.timecardemployeelist

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.EmployeeId
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun TimeCardEmployeeListScreenPreview() = AppTheme {
    TimeCardEmployeeList(
        TimeCardEmployeeListUIState(
            isLoading = true,
            employees = listOf(
                TimeCardEmployeeUIModel(
                    "Cesar Andres Ramirez Sanchez",
                    EmployeeId("John"),
                ),
                TimeCardEmployeeUIModel(
                    "Jane Smith",
                    EmployeeId("Jane"),
                ),
            ),
            title = ""
        ),
        onEmployeeClick = {},
        onCloseSelected = {},
    )
}
