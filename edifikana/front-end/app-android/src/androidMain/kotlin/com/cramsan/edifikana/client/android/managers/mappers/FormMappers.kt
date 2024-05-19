package com.cramsan.edifikana.client.android.managers.mappers

import com.cramsan.edifikana.client.android.models.FieldModel
import com.cramsan.edifikana.client.android.models.FormModel
import com.cramsan.edifikana.lib.firestore.FireStoreModel
import com.cramsan.edifikana.lib.firestore.Form

@FireStoreModel
fun Form.toDomainModel(): FormModel {
    return FormModel(
        id = documentId(),
        name = name ?: TODO("Form name cannot be null"),
        fields = fields?.map {
            FieldModel(
                id = it.id ?: TODO("Field id cannot be null"),
                name = it.name ?: TODO("Field name cannot be null"),
                isRequired = it.required ?: false,
                isSingleLine = it.isSingleLine ?: false
            )
        }.orEmpty()
    )
}
