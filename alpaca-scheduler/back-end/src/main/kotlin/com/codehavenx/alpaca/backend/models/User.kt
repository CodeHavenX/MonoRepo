package com.codehavenx.alpaca.backend.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: UserId,
    val username: String,
)
