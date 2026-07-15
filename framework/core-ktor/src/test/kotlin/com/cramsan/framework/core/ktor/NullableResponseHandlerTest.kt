package com.cramsan.framework.core.ktor

import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.ResponseBody
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.networkapi.Api
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Reproduces a bug where a [Controller] handler that returns `null` to signal "not found" crashes
 * with a 500 instead of the documented 404, because the framework attempts to serialize the null
 * body using the non-nullable response type's generated serializer.
 */
class NullableResponseHandlerTest {
    @Serializable
    private data class DummyResponse(val value: String) : ResponseBody

    private object DummyApi : Api("dummy") {
        val get =
            operation<
                NoRequestBody,
                NoQueryParam,
                NoPathParam,
                DummyResponse,
                >(HttpMethod.Get)
    }

    private val authenticatedContextRetriever =
        object : ContextRetriever<Unit> {
            override suspend fun getContext(token: String) =
                ClientContext.AuthenticatedClientContext(Unit)
        }

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    @Test
    fun `unauthenticatedHandler returning null responds with 404, not 500`() =
        runTest {
            testApplication {
                application {
                    install(ContentNegotiation) { json() }
                    routing {
                        DummyApi.register(this, Unit::class) {
                            unauthenticatedHandler(api.get) { null }
                        }
                    }
                }

                val response = client.get("dummy")

                assertEquals(HttpStatusCode.NotFound, response.status)
            }
        }

    @Test
    fun `handler returning null responds with 404, not 500`() =
        runTest {
            testApplication {
                application {
                    install(ContentNegotiation) { json() }
                    configureBearerAuthentication(authenticatedContextRetriever)
                    routing {
                        DummyApi.register(this, Unit::class) {
                            handler(api.get) { null }
                        }
                    }
                }

                val response = client.get("dummy") { header(HttpHeaders.Authorization, "Bearer test") }

                assertEquals(HttpStatusCode.NotFound, response.status)
            }
        }
}
