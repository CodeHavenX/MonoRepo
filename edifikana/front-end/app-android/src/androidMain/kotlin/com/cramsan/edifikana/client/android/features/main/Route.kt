@file:Suppress("TooManyFunctions")
@file:OptIn(RouteSafePath::class)

package com.cramsan.edifikana.client.android.features.main

import com.cramsan.edifikana.lib.firestore.EmployeePK
import com.cramsan.edifikana.lib.firestore.EventLogRecordPK
import com.cramsan.edifikana.lib.firestore.FormPK
import com.cramsan.edifikana.lib.firestore.FormRecordPK
import com.cramsan.edifikana.lib.requireNotBlank

enum class Route(
    @RouteSafePath
    val route: String,
) {
    TimeCard(route = "clockin"),
    TimeCardEmployeeList(route = "clockin/employees"),
    TimeCardSingleEmployee(route = "clockin/employees/{employeePk}"),
    TimeCardAddEmployee(route = "clockin/add"),
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

        fun toFormEntryRoute(formPk: FormPK): String {
            return FormEntry.route.replace("{formPk}", requireNotBlank(formPk.documentPath))
        }

        fun toFormRecordsRoute(): String = FormRecords.route

        fun toFormRecordReadRoute(
            formRecordPk: FormRecordPK,
        ): String {
            return FormRecordRead.route.replace("{formRecordPk}", requireNotBlank(formRecordPk.documentPath))
        }

        fun toTimeCardRoute(): String = TimeCard.route

        fun toTimeCardEmployeeListRoute(): String = TimeCardEmployeeList.route

        fun toTimeCardSingleEmployeeRoute(employeePk: EmployeePK): String {
            return TimeCardSingleEmployee.route.replace("{employeePk}", requireNotBlank(employeePk.documentPath))
        }

        fun toTimeCardAddEmployeeRoute(): String = TimeCardAddEmployee.route

        fun toEventLogRoute(): String = EventLog.route

        fun toEventLogSingleItemRoute(eventLogRecordPk: EventLogRecordPK): String {
            return EventLogSingleItem.route.replace(
                "{eventLogRecordPk}",
                requireNotBlank(eventLogRecordPk.documentPath)
            )
        }

        fun toEventLogAddItemRoute(): String = EventLogAddItem.route
    }
}
