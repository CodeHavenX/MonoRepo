package com.codehavenx.platform.bot.config

import com.codehavenx.platform.bot.ktor.DateAsStringSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

/**
 * Create a [Json] instance. This function is expected to be used as the single-source-of-truth for how should
 * json be serialized in this project.
 */
fun createJson() = Json {
    serializersModule = SerializersModule {
        contextual(DateAsStringSerializer)
    }
}
