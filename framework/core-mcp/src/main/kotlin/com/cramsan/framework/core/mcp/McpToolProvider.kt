package com.cramsan.framework.core.mcp

import io.ktor.server.application.ApplicationCall
import io.modelcontextprotocol.kotlin.sdk.server.Server

/**
 * Implemented by components (typically controllers) that expose a subset of their operations as MCP tools, in
 * addition to their normal REST routes.
 */
interface McpToolProvider {
    /**
     * Registers this component's MCP tools on [server]. [call] is the [ApplicationCall] for the current MCP
     * request, used to resolve the caller's auth context the same way a REST route would.
     */
    fun registerMcpTools(server: Server, call: ApplicationCall)
}
