package com.cramsan.edifikana.client.lib.managers.mappers

import com.cramsan.edifikana.client.lib.models.FormModel
import com.cramsan.edifikana.client.lib.models.FormRecordFieldModel
import com.cramsan.edifikana.client.lib.models.FormRecordModel
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.FormEntryField
import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.edifikana.lib.firestore.FormRecord
import kotlinx.datetime.Clock

@FireStoreModel
fun FormRecord.toDomainModel(): FormRecordModel {
    return FormRecordModel(
        formRecordPk = formRecordPK(),
        formPk = FormPK(formPk ?: TODO("Form PK cannot be null")),
        timeRecorded = timeRecorded ?: 0,
        name = name ?: TODO("Form name cannot be null"),
        fields = fields?.map {
            FormRecordFieldModel(
                id = it.id ?: TODO("Field id cannot be null"),
                name = it.name ?: TODO("Field name cannot be null"),
                value = it.value.orEmpty(),
            )
        }.orEmpty()
    )
}

@FireStoreModel
fun FormRecordModel.toFirebaseModel(propertyId: String): FormRecord {
    return FormRecord(
        propertyId = propertyId,
        formPk = formPk.documentPath,
        timeRecorded = timeRecorded,
        name = name,
        fields = fields.map {
            FormEntryField(
                id = it.id,
                name = it.name,
                value = it.value,
            )
        }
    )
}

fun createSubmissionFormRecordModel(
    formModel: FormModel,
    clock: Clock,
    fieldNames: Map<String, String>,
    fields: Map<String, String>,
): FormRecordModel {
    return FormRecordModel(
        formRecordPk = null,
        formPk = formModel.id,
        timeRecorded = clock.now().epochSeconds,
        name = formModel.name,
        fields = fields.map {
            FormRecordFieldModel(
                id = it.key,
                name = fieldNames[it.key] ?: it.key,
                value = it.value,
            )
        }
    )
}
