package com.cramsan.framework.core.ktor

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
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.openapi.OpenApiDocSource
import io.ktor.server.routing.openapi.registerJWTSecurityScheme
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
 * Name of the JWT bearer security scheme registered for authenticated operations. Referenced by the
 * per-operation security requirement emitted in the OpenAPI documentation.
 */
internal const val BEARER_SECURITY_SCHEME = "bearerAuth"

/**
 * Configure the OpenApi endpoint
 * https://ktor.io/docs/server-openapi.html
 *
 * @param info Metadata (title/version/description) describing the API, surfaced as the OpenAPI
 * `info` object.
 */
fun Application.configureOpenApiEndpoint(info: ApiInfo) {
    registerJWTSecurityScheme(BEARER_SECURITY_SCHEME)
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
