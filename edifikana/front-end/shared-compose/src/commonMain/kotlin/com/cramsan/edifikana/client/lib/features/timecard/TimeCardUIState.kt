package com.cramsan.edifikana.client.lib.features.timecard

/**
 * Represents the UI state of the Time Card screen.
 */
data class TimeCardUIState(
    val timeCardEvents: List<TimeCardUIModel>,
    val isLoading: Boolean,
    val title: String,
)
