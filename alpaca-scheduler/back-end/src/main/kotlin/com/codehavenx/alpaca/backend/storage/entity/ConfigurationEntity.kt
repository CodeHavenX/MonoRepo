package com.codehavenx.alpaca.backend.storage.entity

data class ConfigurationEntity(
    val id: String,
    val appointmentType: String,
    val duration: Long,
    val timeZone: String,
)
