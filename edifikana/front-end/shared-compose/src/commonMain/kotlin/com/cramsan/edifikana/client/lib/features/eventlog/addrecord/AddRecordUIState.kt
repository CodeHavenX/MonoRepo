package com.cramsan.edifikana.client.lib.features.eventlog.addrecord

/**
 * Represents the UI state of the Add Record screen.
 */
data class AddRecordUIState(
    val records: List<AddRecordUIModel>,
    val isLoading: Boolean,
    val title: String,
)
