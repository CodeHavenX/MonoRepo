package com.cramsan.edifikana.client.lib.features.formlist

data class FormListUIState(
    val forms: List<FormUIModel> = emptyList(),
    val isLoading: Boolean = false,
    val title: String,
)
