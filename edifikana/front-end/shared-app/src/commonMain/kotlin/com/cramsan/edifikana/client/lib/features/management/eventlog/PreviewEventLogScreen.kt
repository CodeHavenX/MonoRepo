package com.cramsan.edifikana.client.lib.features.management.eventlog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.EventLogEntryId
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun PreviewEventLogScreen() = AppTheme {
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
