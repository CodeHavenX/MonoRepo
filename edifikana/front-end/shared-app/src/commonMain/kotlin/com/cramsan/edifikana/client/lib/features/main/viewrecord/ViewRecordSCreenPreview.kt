package com.cramsan.edifikana.client.lib.features.main.viewrecord

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cramsan.edifikana.client.lib.models.AttachmentHolder
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.EventLogEntryId
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun ViewRecordScreenPreview() = AppTheme(debugLayoutInspection = true) {
    SingleRecord(
        ViewRecordUIState(
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
            title = "Delivery of pizza",
            isLoading = false,
        ),
        Modifier,
        {},
        {},
        {},
        {},
    )
}
