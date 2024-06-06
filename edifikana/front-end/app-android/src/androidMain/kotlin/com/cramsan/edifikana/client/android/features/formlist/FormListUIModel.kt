package com.cramsan.edifikana.client.android.features.formlist

import com.cramsan.edifikana.client.lib.models.FormModel
import com.cramsan.edifikana.lib.firestore.FormPK

data class FormUIModel(
    val name: String,
    val formPk: FormPK,
)

fun FormModel.toFormUIModel(): FormUIModel {
    return FormUIModel(
        name = name,
        formPk = id,
    )
}
