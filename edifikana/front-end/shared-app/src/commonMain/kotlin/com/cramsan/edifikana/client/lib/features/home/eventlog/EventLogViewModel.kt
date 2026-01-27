package com.cramsan.edifikana.client.lib.features.home.eventlog

import com.cramsan.edifikana.client.lib.managers.EventLogManager
import com.cramsan.edifikana.client.lib.models.EventLogRecordModel
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.logging.logE
import com.cramsan.framework.logging.logI
import kotlinx.coroutines.launch

/**
 * ViewModel for the EventLog screen.
 */
class EventLogViewModel(dependencies: ViewModelDependencies, private val eventLogManager: EventLogManager) :
    BaseViewModel<EventLogEvent, EventLogUIState>(
        dependencies,
        EventLogUIState.Initial,
        TAG,
    ) {

    /**
     * Load events for the given property.
     */
    fun loadEvents(propertyId: PropertyId) {
        logI(TAG, "Loading events for property: $propertyId")
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true) }

            eventLogManager.getRecords(propertyId)
                .onSuccess { records ->
                    val uiModels = records.map { it.toUIModel() }
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            events = uiModels,
                        )
                    }
                }
                .onFailure { error ->
                    logE(TAG, "Failed to load events", error)
                    updateUiState {
                        it.copy(
                            isLoading = false,
                            events = emptyList(),
                        )
                    }
                }
        }
    }

    /**
     * Handle the add event button click.
     */
    fun onAddEventClicked() {
        logI(TAG, "Add event clicked")
        // TODO: Navigate to add event screen
    }

    companion object {
        private const val TAG = "EventLogViewModel"
    }
}

/**
 * Convert an EventLogRecordModel to an EventLogUIModel.
 */
private fun EventLogRecordModel.toUIModel(): EventLogUIModel {
    val formattedTime = formatTimestamp(timeRecorded)
    return EventLogUIModel(
        id = id,
        title = title,
        description = description,
        eventType = eventType,
        fallbackEventType = fallbackEventType,
        unit = unit,
        timeRecorded = formattedTime,
        employeeName = fallbackEmployeeName,
    )
}

/**
 * Format a timestamp to a human-readable string.
 * Uses simple epoch seconds formatting for multiplatform compatibility.
 */
// TODO: Replace with proper date formatting. The current approach is a placeholder as it only works in english locale.
@Suppress("MagicNumber")
private fun formatTimestamp(timestamp: Long): String {
    // Convert epoch timestamp to a simple date representation
    // Assuming timestamp is in seconds (if larger than reasonable seconds, it's milliseconds)
    val timestampSeconds = if (timestamp > 1_000_000_000_000) timestamp / 1000 else timestamp

    // Simple date calculation from epoch
    val totalDays = timestampSeconds / 86400
    val years = 1970 + (totalDays / 365).toInt()
    val remainingDays = (totalDays % 365).toInt()

    // Approximate month and day
    val monthDays = listOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    var month = 1
    var dayCount = remainingDays
    for (days in monthDays) {
        if (dayCount < days) break
        dayCount -= days
        month++
    }
    val day = dayCount + 1

    // Format time of day
    val secondsInDay = (timestampSeconds % 86400).toInt()
    val hours = secondsInDay / 3600
    val minutes = (secondsInDay % 3600) / 60

    return "$month/$day/$years ${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
}
