package com.cramsan.edifikana.client.android.models

import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.edifikana.lib.firestore.FormRecordPK

data class FormRecordModel(
    val formRecordPk: FormRecordPK?,
    val formPk: FormPK,
    val timeRecorded: Long,
    val name: String,
    val fields: List<FormRecordFieldModel>,
)

data class FormRecordFieldModel(
    val id: String,
    val name: String,
    val value: String,
)
