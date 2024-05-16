package com.cramsan.edifikana.client.android.features.timecard

data class TimeCardUIState(
    val employees: List<TimeCardUIModel>,
    val isLoading: Boolean,
)