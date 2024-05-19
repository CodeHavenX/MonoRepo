package com.cramsan.edifikana.client.android.features.formlist.entry

import com.cramsan.edifikana.client.android.models.FormModel

data class EntryUIModel(
    val name: String,
    val fields: List<EntryFieldUIModel>,
    val submitAllowed: Boolean,
)

data class EntryFieldUIModel(
    val fieldId: String,
    val name: String,
    val isRequired: Boolean,
    val isSingleLine: Boolean,
)

fun FormModel.toEntryUIModel(): EntryUIModel {
    return EntryUIModel(
        name = name,
        submitAllowed = false,
        fields = fields.map {
            EntryFieldUIModel(
                fieldId = it.id,
                name = it.name,
                isRequired = it.isRequired,
                isSingleLine = it.isSingleLine,
            )
        }
    )
}
