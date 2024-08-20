package com.codehavenx.alpaca.backend.core.controller

import com.codehavenx.alpaca.backend.core.service.models.StaffId
import kotlinx.datetime.LocalDateTime

/**
 * Converts a string to a local date time.
 */
fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this)
}

/**
 * Converts a string to a staff id.
 */
fun String.toStaffId(): StaffId {
    return StaffId(this)
}
