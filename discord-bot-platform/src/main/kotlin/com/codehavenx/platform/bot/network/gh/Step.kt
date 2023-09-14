package com.codehavenx.platform.bot.network.gh

import kotlinx.serialization.SerialName
import java.util.Date

data class Step(
    val name: String? = null,
    val status: String? = null,
    val conclusion: String? = null,
    val number: Long = 0,
    @SerialName("started_at")
    val startedAt: Date? = null,
    @SerialName("completed_at")
    val completedAt: Date? = null,
)
