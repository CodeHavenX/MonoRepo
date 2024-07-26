package com.codehavenx.alpaca.backend.controller

import com.codehavenx.alpaca.backend.models.AppointmentType
import com.codehavenx.alpaca.backend.models.StaffId
import kotlinx.datetime.LocalDateTime

fun String.toAppointmentType(): AppointmentType {
    return AppointmentType(this)
}

fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this)
}

fun String.toStaffId(): StaffId {
    return StaffId(this)
}
