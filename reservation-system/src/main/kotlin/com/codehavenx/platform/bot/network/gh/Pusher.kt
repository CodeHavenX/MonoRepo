package com.codehavenx.platform.bot.network.gh

import kotlinx.serialization.Serializable

@Serializable
data class Pusher(
    val name: String? = null,
    val email: String? = null,
)
