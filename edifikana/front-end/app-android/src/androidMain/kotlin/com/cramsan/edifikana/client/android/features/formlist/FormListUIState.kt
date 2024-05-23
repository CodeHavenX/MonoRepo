package com.cramsan.edifikana.client.android.features.formlist

data class FormListUIState(
    val forms: List<FormUIModel> = emptyList(),
    val isLoading: Boolean = false,
)
