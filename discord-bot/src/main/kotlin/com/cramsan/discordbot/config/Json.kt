package com.cramsan.discordbot.config

import kotlinx.serialization.json.Json

/**
 * Create a [Json] instance. This function is expected to be used as the single-source-of-truth for how should
 * json be serialized in this project.
 */
fun createJson() = Json {
    ignoreUnknownKeys = true
    isLenient = true
    prettyPrint = true
}