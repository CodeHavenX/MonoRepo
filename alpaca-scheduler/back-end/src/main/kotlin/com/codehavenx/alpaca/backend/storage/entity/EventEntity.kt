package com.codehavenx.alpaca.backend.storage.entity

data class EventEntity(
    val id: String,
    val owner: String,
    val attendants: Set<String>,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
)
