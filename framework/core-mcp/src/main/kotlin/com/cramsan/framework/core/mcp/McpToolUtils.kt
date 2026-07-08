package com.cramsan.framework.core.mcp

import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.PathParam
import com.cramsan.framework.annotations.api.ResponseBody
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.requireAuthenticatedClientContext
import com.cramsan.framework.networkapi.Operation
import io.ktor.openapi.JsonSchema
import io.ktor.openapi.buildJsonSchema
import io.ktor.server.application.ApplicationCall
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.ToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import io.modelcontextprotocol.kotlin.sdk.types.error
import io.modelcontextprotocol.kotlin.sdk.types.success
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

/**
 * Registers [operation] — a GET-style operation with no request body and no query parameters — as an MCP tool
 * named [name] on this [Server]. Reuses [contextRetriever] to resolve the same [ClientContext] the operation's
 * REST route uses, and delegates to [handler] (typically the same controller method that backs the REST route),
 * so no business logic is duplicated between the two transports.
 *
 * @param pathParamName the argument name exposed to MCP clients for the operation's path parameter. Required
 * when [operation] has a path parameter; must be left `null` for parameterless operations.
 */
@Suppress("LongParameterList")
fun <PathParamType : PathParam, ResponseType : ResponseBody, P> Server.mcpTool(
    operation: Operation<NoRequestBody, NoQueryParam, PathParamType, ResponseType>,
    name: String,
    description: String,
    call: ApplicationCall,
    contextRetriever: ContextRetriever<P>,
    json: Json,
    pathParamName: String? = null,
    handler: suspend (
        OperationRequest<NoRequestBody, NoQueryParam, PathParamType, ClientContext.AuthenticatedClientContext<P>>,
    ) -> ResponseType?,
) {
    addTool(
        name = name,
        description = description,
        inputSchema = buildMcpInputSchema(operation.pathParamType, pathParamName, json),
        toolAnnotations = ToolAnnotations(readOnlyHint = true, destructiveHint = false),
    ) { request ->
        handleMcpToolCall(operation, pathParamName, call, contextRetriever, json, request, handler)
    }
}

@OptIn(InternalSerializationApi::class)
@Suppress("LongParameterList")
private suspend fun <PathParamType : PathParam, ResponseType : ResponseBody, P> handleMcpToolCall(
    operation: Operation<NoRequestBody, NoQueryParam, PathParamType, ResponseType>,
    pathParamName: String?,
    call: ApplicationCall,
    contextRetriever: ContextRetriever<P>,
    json: Json,
    request: CallToolRequest,
    handler: suspend (
        OperationRequest<NoRequestBody, NoQueryParam, PathParamType, ClientContext.AuthenticatedClientContext<P>>,
    ) -> ResponseType?,
): CallToolResult {
    val authContext =
        runCatching {
            requireAuthenticatedClientContext(contextRetriever.getContext(call))
        }.getOrElse {
            return CallToolResult.error("Unauthorized: ${it.message ?: "Unknown error"}")
        }

    val pathParam =
        runCatching {
            decodeMcpPathParam(operation.pathParamType, pathParamName, request.arguments, json)
        }.getOrElse {
            return CallToolResult.error("Invalid arguments: ${it.message ?: "Unknown error"}")
        }

    val operationRequest =
        OperationRequest(
            requestBody = NoRequestBody,
            queryParam = NoQueryParam,
            pathParam = pathParam,
            context = authContext,
        )

    val response =
        runCatching { handler(operationRequest) }
            .getOrElse { return CallToolResult.error(it.message ?: "Unknown error") }
            ?: return CallToolResult.error("Not found.")

    val responseJson = json.encodeToString(operation.responseBodyType.serializer(), response)
    return CallToolResult.success(responseJson)
}

@OptIn(InternalSerializationApi::class)
private fun <PathParamType : PathParam> buildMcpInputSchema(
    pathParamType: KClass<PathParamType>,
    pathParamName: String?,
    json: Json,
): ToolSchema {
    if (pathParamName == null) {
        return ToolSchema()
    }
    val fieldSchema: JsonSchema = pathParamType.serializer().descriptor.buildJsonSchema(visiting = mutableSetOf())
    val fieldSchemaJson = json.encodeToJsonElement(JsonSchema.serializer(), fieldSchema).jsonObject
    return ToolSchema(
        properties = buildJsonObject { put(pathParamName, fieldSchemaJson) },
        required = listOf(pathParamName),
    )
}

@OptIn(InternalSerializationApi::class)
private fun <PathParamType : PathParam> decodeMcpPathParam(
    pathParamType: KClass<PathParamType>,
    pathParamName: String?,
    arguments: JsonObject?,
    json: Json,
): PathParamType {
    if (pathParamName == null) {
        check(pathParamType == NoPathParam::class) {
            "Operation misconfiguration: pathParamType is $pathParamType but no pathParamName was provided."
        }
        @Suppress("UNCHECKED_CAST")
        return NoPathParam as PathParamType
    }
    val value =
        arguments?.get(pathParamName)
            ?: error("Missing required argument '$pathParamName'.")
    return json.decodeFromJsonElement(pathParamType.serializer(), value)
}
