package com.cramsan.edifikana.client.lib.features.formlist.records.read

import com.cramsan.edifikana.client.lib.models.FormRecordModel

data class RecordReadUIModel(
    val name: String,
    val fields: List<RecordFieldUIModel>,
)

data class RecordFieldUIModel(
    val name: String,
    val value: String,
)

fun FormRecordModel.toReadRecordUIModel(): RecordReadUIModel {
    return RecordReadUIModel(
        name = name,
        fields = fields.map { RecordFieldUIModel(it.name, it.value) },
    )
}
