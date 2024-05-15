package com.cramsan.edifikana.client.android.features.main

enum class Route(val route: String) {
    ClockInOut(route = "clockin"),
    ClockInOutSingleEmployee(route = "clockin/{employeePk}"),
    ClockInOutAddEmployee(route = "clockin/add"),
    EventLog(route = "eventlog"),
    EventLogSingleItem(route = "eventlog/{eventLogRecordPk}"),
    EventLogAddItem(route = "eventlog/add"),
}
