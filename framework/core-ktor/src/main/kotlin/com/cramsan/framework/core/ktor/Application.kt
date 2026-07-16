package com.cramsan.framework.core.ktor

import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.logging.EventLoggerInterface
import com.cramsan.framework.networkapi.ApiInfo
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.ServerReady
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.bearer
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.openapi.OpenApiDocSource
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.routing.routingRoot

/**
 * Initialize monitoring
 */
fun Application.initializeMonitoring(
    tag: String,
    logger: EventLoggerInterface,
) {
    monitor.subscribe(ApplicationStarting) {
        logger.i(tag, "ApplicationStarting")
    }
    monitor.subscribe(ApplicationStarted) {
        logger.i(tag, "ApplicationStarted")
    }
    monitor.subscribe(ServerReady) {
        logger.i(tag, "ServerReady")
    }
    monitor.subscribe(ApplicationStopPreparing) {
        logger.i(tag, "ApplicationStopPreparing")
    }
    monitor.subscribe(ApplicationStopping) {
        logger.i(tag, "ApplicationStopping")
    }
    monitor.subscribe(ApplicationStopped) {
        logger.i(tag, "ApplicationStopped")
    }
}

/**
 * Configure an endpoint to check for the application health.
 */
fun Application.configureHealthEndpoint() {
    routing {
        route("/health") {
            get {
                call.respond(HttpStatusCode.OK, "OK")
            }
            head {
                call.respond(HttpStatusCode.OK, "OK")
            }
        }
    }
}

/**
 * Name of the bearer authentication provider and its corresponding OpenAPI security scheme. The
 * provider is installed by [configureBearerAuthentication]; routes registered under
 * `authenticate(BEARER_SECURITY_SCHEME)` are protected and have their security requirement inferred
 * into the OpenAPI documentation automatically.
 */
const val BEARER_SECURITY_SCHEME = "bearerAuth"

/**
 * Installs the bearer authentication provider used to protect authenticated operations.
 *
 * The provider extracts the token from the `Authorization: Bearer <token>` header and delegates to the
 * given [contextRetriever] to exchange it for a [ClientContext]. A valid token yields an authenticated
 * principal; a rejected token (unauthenticated context) triggers a 401 challenge. Failures thrown by
 * the retriever (e.g. the auth provider being unreachable) propagate and surface as a 5xx.
 *
 * Because a standard bearer provider is used, its OpenAPI security scheme is inferred automatically;
 * no manual scheme registration is required.
 *
 * @param contextRetriever Resolves the client context from the validated bearer token.
 */
fun Application.configureBearerAuthentication(contextRetriever: ContextRetriever<*>) {
    install(Authentication) {
        bearer(BEARER_SECURITY_SCHEME) {
            authenticate { credential ->
                when (val context = contextRetriever.getContext(credential.token)) {
                    is ClientContext.AuthenticatedClientContext<*> -> context
                    is ClientContext.UnauthenticatedClientContext<*> -> null
                }
            }
        }
    }
}

/**
 * Configure the OpenApi endpoint
 * https://ktor.io/docs/server-openapi.html
 *
 * @param info Metadata (title/version/description) describing the API, surfaced as the OpenAPI
 * `info` object.
 */
fun Application.configureOpenApiEndpoint(info: ApiInfo) {
    val openApiInfo =
        OpenApiInfo(
            title = info.title,
            version = info.version,
            description = info.description,
            contact = null,
        )
    routing {
        swaggerUI("swaggerUI") {
            this.info = openApiInfo
            source =
                OpenApiDocSource.Routing {
                    routingRoot.descendants()
                }
        }
    }
}
