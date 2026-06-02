package com.cramsan.templatereplaceme.server.service.models

import com.cramsan.templatereplaceme.lib.model.PingPong

/**
 * Domain model representing a pong.
 */
data class Pong(
    val id: PingPong,
    val firstName: String,
    val lastName: String,
)
