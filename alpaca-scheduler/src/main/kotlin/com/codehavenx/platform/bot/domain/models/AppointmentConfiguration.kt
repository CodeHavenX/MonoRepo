package com.codehavenx.platform.bot.domain.models

import kotlin.time.Duration

data class AppointmentConfiguration(
    val preDuration: Duration,
    val duration: Duration,
    val postDuration: Duration,
)
