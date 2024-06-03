package com.cramsan.edifikana.client.android.features.eventlog.addrecord

data class AddRecordUIState(
    val employees: List<AddRecordUIModel>,
    val isLoading: Boolean,
    val title: String,
)
