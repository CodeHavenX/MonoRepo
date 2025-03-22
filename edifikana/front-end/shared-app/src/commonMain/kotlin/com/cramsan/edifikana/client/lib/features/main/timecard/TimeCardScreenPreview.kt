package com.cramsan.edifikana.client.lib.features.main.timecard

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.StaffId
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun TimeCardScreenPreview() = AppTheme {
    EventList(
        isLoading = true,
        events = listOf(
            TimeCardUIModel(
                "Cesar Andres Ramirez Sanchez",
                "Marco salida",
                "2024 02 12 - 03:24:01",
                StaffId("John"),
            ),
            TimeCardUIModel(
                "Antonio",
                "Marco entrada",
                "2024 02 12 - 03:24:01",
                StaffId("Jane"),
            ),
        ),
        onStaffClick = {},
        onAddEventClick = {},
    )
}
