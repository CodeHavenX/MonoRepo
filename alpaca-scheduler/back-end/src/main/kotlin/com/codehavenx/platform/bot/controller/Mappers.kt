package com.codehavenx.platform.bot.controller

import com.codehavenx.platform.bot.domain.models.AppointmentType
import com.codehavenx.platform.bot.domain.models.StaffId
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
