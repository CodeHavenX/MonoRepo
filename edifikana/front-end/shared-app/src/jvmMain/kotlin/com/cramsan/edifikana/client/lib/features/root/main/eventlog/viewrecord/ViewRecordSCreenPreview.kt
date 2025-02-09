package com.cramsan.edifikana.client.lib.features.root.main.eventlog.viewrecord

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.lib.features.main.eventlog.viewrecord.SingleRecord
import com.cramsan.edifikana.client.lib.features.main.eventlog.viewrecord.ViewRecordUIModel
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.lib.model.EventLogEntryId

@Preview
@Composable
private fun ViewRecordScreenPreview() {
    SingleRecord(
        false,
        Modifier,
        ViewRecordUIModel(
            title = "Delivery of pizza",
            eventType = "Invitado",
            timeRecorded = "2024 12 02 12:12:12",
            unit = "302",
            description = "Pizza delivery to the main entrance. The delivery was made by the main entrance. ",
            attachments = listOf(
                AttachmentHolder("url", "url"),
                AttachmentHolder("url", "url"),
                AttachmentHolder("url", "url"),
            ),
            recordPK = EventLogEntryId("1"),
        ),
        {},
        {},
        {},
    )
}
