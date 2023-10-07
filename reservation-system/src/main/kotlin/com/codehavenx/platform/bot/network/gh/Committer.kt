package com.codehavenx.platform.bot.network.gh

import kotlinx.serialization.Serializable

@Serializable
data class Committer(
    val name: String? = null,
    val email: String? = null,
    val username: String? = null,
)
