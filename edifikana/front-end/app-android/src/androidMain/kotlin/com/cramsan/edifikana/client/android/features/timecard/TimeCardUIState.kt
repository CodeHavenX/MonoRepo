package com.cramsan.edifikana.client.android.features.timecard

data class TimeCardUIState(
    val timeCardEvents: List<TimeCardUIModel>,
    val isLoading: Boolean,
    val title: String,
)
