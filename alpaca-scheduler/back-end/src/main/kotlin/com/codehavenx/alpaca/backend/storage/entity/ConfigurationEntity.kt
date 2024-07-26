package com.codehavenx.alpaca.backend.storage.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class ConfigurationEntity(
    @BsonId val id: ObjectId = ObjectId(),
    val appointmentType: String,
    val duration: Long,
    val timeZone: String,
)
