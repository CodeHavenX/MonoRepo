package com.cramsan.edifikana.client.lib.features.formlist.records

data class RecordsUIState(
    val content: List<RecordsUIModel>,
    val isLoading: Boolean,
    val title: String,
)
