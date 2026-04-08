package com.cramsan.edifikana.client.lib.features.home.eventlog

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.eventLog.EventLogEntryId
import com.cramsan.edifikana.lib.model.eventLog.EventLogEventType
import com.cramsan.edifikana.lib.model.unit.UnitId
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun EventLogScreenPreview_Loading() = AppTheme {
    EventLogContent(
        uiState = EventLogUIState(
            isLoading = true,
            events = emptyList(),
        ),
        onAddEventClick = {},
    )
}

@Preview
@Composable
private fun EventLogScreenPreview_Empty() = AppTheme {
    EventLogContent(
        uiState = EventLogUIState(
            isLoading = false,
            events = emptyList(),
        ),
        onAddEventClick = {},
    )
}

@Preview
@Composable
private fun EventLogScreenPreview_WithEvents() = AppTheme {
    EventLogContent(
        uiState = EventLogUIState(
            isLoading = false,
            events = listOf(
                EventLogUIModel(
                    id = EventLogEntryId("event-1"),
                    title = "Water Leak Detected",
                    description = "Water leak found in the basement near the main pipe.",
                    eventType = EventLogEventType.INCIDENT,
                    fallbackEventType = null,
                    unit = UnitId("Basement"),
                    timeRecorded = "Dec 15, 2025 10:30 AM",
                    employeeName = "Juan Perez",
                ),
                EventLogUIModel(
                    id = EventLogEntryId("event-2"),
                    title = "Guest Arrival",
                    description = "Guest arrived for apartment 301.",
                    eventType = EventLogEventType.GUEST,
                    fallbackEventType = null,
                    unit = UnitId("Apt 301"),
                    timeRecorded = "Dec 15, 2025 09:15 AM",
                    employeeName = "Maria Garcia",
                ),
                EventLogUIModel(
                    id = EventLogEntryId("event-3"),
                    title = "Package Delivery",
                    description = "",
                    eventType = EventLogEventType.DELIVERY,
                    fallbackEventType = null,
                    unit = UnitId("Lobby"),
                    timeRecorded = "Dec 14, 2025 04:45 PM",
                    employeeName = null,
                ),
                EventLogUIModel(
                    id = EventLogEntryId("event-4"),
                    title = "Maintenance Request",
                    description = "AC unit not working properly, needs inspection.",
                    eventType = EventLogEventType.MAINTENANCE_SERVICE,
                    fallbackEventType = "HVAC Issue",
                    unit = UnitId("Apt 505"),
                    timeRecorded = "Dec 14, 2025 02:00 PM",
                    employeeName = "Carlos Rodriguez",
                ),
            ),
        ),
        onAddEventClick = {},
    )
}

@Preview(locale = "es")
@Composable
private fun EventLogScreenPreview_Empty_ES() = AppTheme {
    EventLogContent(
        uiState = EventLogUIState(
            isLoading = false,
            events = emptyList(),
        ),
        onAddEventClick = {},
    )
}

@Preview(locale = "es")
@Composable
private fun EventLogScreenPreview_WithEvents_ES() = AppTheme {
    EventLogContent(
        uiState = EventLogUIState(
            isLoading = false,
            events = listOf(
                EventLogUIModel(
                    id = EventLogEntryId("event-1"),
                    title = "Fuga de agua detectada",
                    description = "Fuga de agua encontrada en el sótano cerca de la tubería principal.",
                    eventType = EventLogEventType.INCIDENT,
                    fallbackEventType = null,
                    unit = UnitId("Sótano"),
                    timeRecorded = "15 dic. 2025 10:30",
                    employeeName = "Juan Pérez",
                ),
                EventLogUIModel(
                    id = EventLogEntryId("event-2"),
                    title = "Llegada de visita",
                    description = "Visita llegó al departamento 301.",
                    eventType = EventLogEventType.GUEST,
                    fallbackEventType = null,
                    unit = UnitId("Dpto 301"),
                    timeRecorded = "15 dic. 2025 09:15",
                    employeeName = null,
                ),
            ),
        ),
        onAddEventClick = {},
    )
}

@Preview
@Composable
private fun EventLogScreenPreview_SingleEvent() = AppTheme {
    EventLogContent(
        uiState = EventLogUIState(
            isLoading = false,
            events = listOf(
                EventLogUIModel(
                    id = EventLogEntryId("event-1"),
                    title = "Security Alert",
                    description = "Unauthorized access attempt at the main gate.",
                    eventType = EventLogEventType.INCIDENT,
                    fallbackEventType = null,
                    unit = UnitId("Main Gate"),
                    timeRecorded = "Dec 15, 2025 11:45 AM",
                    employeeName = "Pedro Sanchez",
                ),
            ),
        ),
        onAddEventClick = {},
    )
}
