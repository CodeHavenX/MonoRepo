package com.codehavenx.platform.bot.ktor

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.Date

object DateAsStringSerializer : KSerializer<Date> {
    @Suppress("SimpleDateFormat")
    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Date) = encoder.encodeString(format.format(value))
    override fun deserialize(decoder: Decoder): Date = Date.from(
        OffsetDateTime.parse(decoder.decodeString()).toInstant(),
    )
}
