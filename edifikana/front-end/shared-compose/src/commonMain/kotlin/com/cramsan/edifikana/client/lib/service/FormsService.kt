package com.cramsan.edifikana.client.lib.service

import com.cramsan.edifikana.client.lib.models.FormModel
import com.cramsan.edifikana.client.lib.models.FormRecordModel
import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.edifikana.lib.firestore.FormRecordPK

interface FormsService {

    suspend fun getForms(): Result<List<FormModel>>

    suspend fun getForm(formPK: FormPK): Result<FormModel>

    suspend fun getFormRecords(): Result<List<FormRecordModel>>

    suspend fun getFormRecord(formRecordPK: FormRecordPK): Result<FormRecordModel>

    suspend fun submitFormRecord(formRecordModel: FormRecordModel): Result<Unit>
}
