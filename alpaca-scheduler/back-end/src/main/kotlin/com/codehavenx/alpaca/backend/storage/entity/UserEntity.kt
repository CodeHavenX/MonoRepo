package com.codehavenx.alpaca.backend.storage.entity

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class UserEntity(
    @BsonId val id: ObjectId = ObjectId(),
    val name: String,
)
