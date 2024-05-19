package com.cramsan.edifikana.client.android.features.formlist.records

import com.cramsan.edifikana.client.android.models.FormRecordModel
import com.cramsan.edifikana.client.lib.toFriendlyDateTime

data class RecordsUIModel(
    val name: String,
    val timeRecorded: String,
    val snippet: String,
    val recordModel: FormRecordModel,
)

fun FormRecordModel.toUIModel(): RecordsUIModel {
    return RecordsUIModel(
        name = name,
        timeRecorded = timeRecorded.toFriendlyDateTime(),
        recordModel = this,
        snippet = fields.joinToString(", ") {
            "${it.name} ${it.value}"
        }.let {
            if (it.length > 50) it.substring(0, 50) + "..." else it
        }
    )
}