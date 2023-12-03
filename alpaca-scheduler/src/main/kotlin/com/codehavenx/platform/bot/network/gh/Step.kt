package com.codehavenx.platform.bot.network.gh

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Step(
    val name: String? = null,
    val status: String? = null,
    val conclusion: String? = null,
    val number: Long = 0,
    @SerialName("started_at")
    @Contextual
    val startedAt: Date? = null,
    @Contextual
    @SerialName("completed_at")
    val completedAt: Date? = null,
)
