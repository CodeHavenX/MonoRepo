package com.cramsan.edifikana.client.lib.features.main.timecard

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.lib.model.StaffId

@Preview
@Composable
private fun TimeCardScreenPreview() {
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
