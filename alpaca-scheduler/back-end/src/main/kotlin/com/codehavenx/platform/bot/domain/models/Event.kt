package com.codehavenx.platform.bot.domain.models

import kotlinx.datetime.LocalDateTime

data class Event(
    val id: String,
    val owner: StaffId,
    val attendants: Set<UserId>,
    val title: String,
    val description: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
)
