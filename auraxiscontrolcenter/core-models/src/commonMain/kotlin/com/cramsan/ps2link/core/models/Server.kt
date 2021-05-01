package com.cramsan.ps2link.core.models

/**
 * @Author cramsan
 * @created 1/30/2021
 */
data class Server(
    val worldId: String,
    val namespace: Namespace,
    val serverName: String? = null,
    val serverMetadata: ServerMetadata? = null,
)