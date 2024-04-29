package com.cramsan.edifikana.client.android

enum class Screens(val route: String) {
    ClockInOut(route = "clockin"),
    ClockInOutSingleEmployee(route = "clockin/{employeeId}"),
    EventLog(route = "eventlog"),
    EventLogSingleItem(route = "eventlog/{itemId}"),
    EventLogAddItem(route = "eventlog/add"),
}
