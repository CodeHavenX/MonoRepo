package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.firestore.FormPK

data class FormModel(
    val id: FormPK,
    val name: String,
    val fields: List<FieldModel>,
)

data class FieldModel(
    val id: String,
    val name: String,
    val isRequired: Boolean,
    val isSingleLine: Boolean,
)
