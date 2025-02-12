package com.cramsan.edifikana.client.lib.features.main.eventlog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.lib.model.EventLogEntryId

@Preview
@Composable
private fun PreviewEventLogScreen() {
    RecordList(
        modifier = Modifier,
        records = listOf(
            EventLogRecordUIModel(
                "Arrived package for dpt 1801",
                "DELIVERY",
                "1801",
                "2021-09-01T00:00:00Z",
                EventLogEntryId("1"),
                true,
            ),
            EventLogRecordUIModel(
                "Arrived package for dpt 1801",
                "DELIVERY",
                "1801",
                "2021-09-01T00:00:00Z",
                EventLogEntryId("1"),
                false,
            ),
        ),
        isLoading = true,
        onRecordSelected = { },
        onAddRecordClicked = { },
    )
}
