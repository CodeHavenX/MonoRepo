package com.codehavenx.alpaca.backend.storage.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class EventEntity(
    @BsonId val id: ObjectId = ObjectId(),
    val owner: String,
    val attendants: Set<String>,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
)
