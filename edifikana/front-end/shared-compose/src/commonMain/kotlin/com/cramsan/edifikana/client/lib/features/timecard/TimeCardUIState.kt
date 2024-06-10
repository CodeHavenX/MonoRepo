package com.cramsan.edifikana.client.lib.features.timecard

data class TimeCardUIState(
    val timeCardEvents: List<TimeCardUIModel>,
    val isLoading: Boolean,
    val title: String,
)
