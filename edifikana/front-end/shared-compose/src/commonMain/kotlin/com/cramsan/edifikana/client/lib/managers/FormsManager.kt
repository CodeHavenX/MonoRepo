package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.FormModel
import com.cramsan.edifikana.client.lib.models.FormRecordModel
import com.cramsan.edifikana.client.lib.service.FormsService
import com.cramsan.edifikana.client.lib.utils.getOrCatch
import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.edifikana.lib.firestore.FormRecordPK
import com.cramsan.framework.logging.logI

class FormsManager(
    private val formsService: FormsService,
    private val workContext: WorkContext,
) {
    suspend fun getForms(): Result<List<FormModel>> = workContext.getOrCatch(TAG) {
        logI(TAG, "getForms")
        formsService.getForms().getOrThrow()
    }

    suspend fun getForm(formPK: FormPK): Result<FormModel> = workContext.getOrCatch(TAG) {
        logI(TAG, "getForm")
        formsService.getForm(formPK).getOrThrow()
    }

    suspend fun getFormRecords(): Result<List<FormRecordModel>> = workContext.getOrCatch(TAG) {
        logI(TAG, "getFormRecords")
        formsService.getFormRecords().getOrThrow()
    }

    suspend fun getFormRecord(formRecordPK: FormRecordPK): Result<FormRecordModel> = workContext.getOrCatch(TAG) {
        logI(TAG, "getFormRecord")
        formsService.getFormRecord(formRecordPK).getOrThrow()
    }

    suspend fun submitFormRecord(formRecordModel: FormRecordModel): Result<Unit> = workContext.getOrCatch(TAG) {
        logI(TAG, "submitFormRecord")
        formsService.submitFormRecord(formRecordModel).getOrThrow()
    }

    companion object {
        private const val TAG = "FormsManager"
    }
}
