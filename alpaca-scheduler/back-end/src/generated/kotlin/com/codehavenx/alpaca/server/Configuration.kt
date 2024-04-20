package com.codehavenx.alpaca.server

// Use this file to hold package-level internal functions that return receiver object passed to the `install` method.
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.CompressionConfig
import io.ktor.server.plugins.compression.deflate
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.compression.minimumSize
import io.ktor.server.plugins.hsts.HSTS
import io.ktor.server.plugins.hsts.HSTSConfig
import java.util.concurrent.TimeUnit

/**
 * Application block for [HSTS] configuration.
 *
 * This file may be excluded in .openapi-generator-ignore,
 * and application-specific configuration can be applied in this function.
 *
 * See http://ktor.io/features/hsts.html
 */
internal fun ApplicationHstsConfiguration(): HSTSConfig.() -> Unit {
    return {
        maxAgeInSeconds = TimeUnit.DAYS.toSeconds(365)
        includeSubDomains = true
        preload = false

        // You may also apply any custom directives supported by specific user-agent. For example:
        // customDirectives.put("redirectHttpToHttps", "false")
    }
}

/**
 * Application block for [Compression] configuration.
 *
 * This file may be excluded in .openapi-generator-ignore,
 * and application-specific configuration can be applied in this function.
 *
 * See http://ktor.io/features/compression.html
 */
internal fun ApplicationCompressionConfiguration(): CompressionConfig.() -> Unit {
    return {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }
}
