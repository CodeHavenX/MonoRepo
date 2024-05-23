package com.cramsan.edifikana.client.android.features.main

import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.edifikana.lib.firestore.FormRecordPK

enum class Route(val route: String) {
    ClockInOut(route = "clockin"),
    ClockInOutSingleEmployee(route = "clockin/{employeePk}"),
    ClockInOutAddEmployee(route = "clockin/add"),
    EventLog(route = "eventlog"),
    EventLogSingleItem(route = "eventlog/{eventLogRecordPk}"),
    EventLogAddItem(route = "eventlog/add"),
    Forms(route = "forms"),
    FormEntry(route = "forms/{formPk}"),
    FormRecords(route = "forms/records"),
    FormRecordRead(route = "forms/records/{formRecordPk}"),
    ;
    companion object {
        fun toFormsRoute(): String = Forms.route

        fun toFormEntryRoute(formPk: FormPK): String = FormEntry.route.replace("{formPk}", formPk.documentPath)

        fun toFormRecordsRoute(): String = FormRecords.route

        fun toFormRecordReadRoute(
            formRecordPk: FormRecordPK,
        ): String = FormRecordRead.route.replace("{formRecordPk}", formRecordPk.documentPath)
    }
}
