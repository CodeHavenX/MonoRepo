package com.cramsan.edifikana.server

import com.cramsan.framework.core.mcp.McpToolProvider
import io.ktor.server.application.Application
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcpStatelessStreamableHttp
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities

/**
 * Mounts the MCP Streamable HTTP endpoint at `/mcp`, backed by every registered [McpToolProvider]. Each request
 * builds a fresh [Server] (stateless transport), so tool calls re-resolve the caller's auth context the same
 * way a REST request does, rather than pinning it to whatever was true when a session was first established.
 */
fun Application.configureMcpEndpoint(mcpToolProviders: List<McpToolProvider>) {
    mcpStatelessStreamableHttp(path = "/mcp") {
        val server =
            Server(
                serverInfo = Implementation(name = "edifikana-mcp", version = "1.0.0"),
                options =
                ServerOptions(
                    capabilities = ServerCapabilities(tools = ServerCapabilities.Tools(listChanged = false)),
                ),
            )
        mcpToolProviders.forEach { it.registerMcpTools(server, call) }
        server
    }
}
