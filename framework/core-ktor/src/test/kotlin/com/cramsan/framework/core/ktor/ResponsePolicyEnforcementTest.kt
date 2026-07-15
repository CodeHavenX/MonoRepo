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
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifies that an operation's strict response policy is enforced at runtime: a response whose
 * status is not declared (nor part of the universal set) is coerced to a 500, while declared
 * responses and the permissive [com.cramsan.framework.networkapi.AllowAnyResponse] policy pass
 * through.
 */
class ResponsePolicyEnforcementTest {
    @Serializable
    private data class DummyResponse(val value: String) : ResponseBody

    private object DummyApi : Api("dummy") {
        val strictNoDomainCodes =
            operation<NoRequestBody, NoQueryParam, NoPathParam, DummyResponse>(
                HttpMethod.Get,
                path = "strict",
                responses = UniversalResponsesOnly,
            )

        val declaresNotFound =
            operation<NoRequestBody, NoQueryParam, NoPathParam, DummyResponse>(
                HttpMethod.Get,
                path = "declares-not-found",
                responses =
                AdditionalResponses {
                    HttpStatusCode.NotFound describedAs "Not found."
                },
            )

        val declaresForbidden =
            operation<NoRequestBody, NoQueryParam, NoPathParam, DummyResponse>(
                HttpMethod.Get,
                path = "declares-forbidden",
                responses =
                AdditionalResponses {
                    HttpStatusCode.Forbidden describedAs "Forbidden."
                },
            )

        val allowAll =
            operation<NoRequestBody, NoQueryParam, NoPathParam, DummyResponse>(
                HttpMethod.Get,
                path = "allow-all",
            )
    }

    private val authenticatedContextRetriever =
        object : ContextRetriever<Unit> {
            override suspend fun getContext(token: String) =
                ClientContext.AuthenticatedClientContext(Unit)
        }

    private fun ApplicationTestBuilder.setupApp(
        block: OperationHandler.RegistrationBuilder<DummyApi, Unit>.() -> Unit,
    ) {
        application {
            install(ContentNegotiation) { json() }
            configureBearerAuthentication(authenticatedContextRetriever)
            routing {
                DummyApi.register(this, Unit::class) { block() }
            }
        }
    }

    private suspend fun ApplicationTestBuilder.getAuthenticated(path: String): HttpResponse =
        client.get(path) { header(HttpHeaders.Authorization, "Bearer test") }

    @BeforeTest
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    @Test
    fun `strict policy coerces an undeclared 404 to 500`() =
        runTest {
            testApplication {
                setupApp { handler(api.strictNoDomainCodes) { null } }

                val response = getAuthenticated("dummy/strict")

                assertEquals(HttpStatusCode.InternalServerError, response.status)
            }
        }

    @Test
    fun `strict policy allows a declared 404`() =
        runTest {
            testApplication {
                setupApp { handler(api.declaresNotFound) { null } }

                val response = getAuthenticated("dummy/declares-not-found")

                assertEquals(HttpStatusCode.NotFound, response.status)
            }
        }

    @Test
    fun `strict policy coerces an undeclared thrown status to 500`() =
        runTest {
            testApplication {
                setupApp {
                    handler(api.strictNoDomainCodes) {
                        throw ClientRequestExceptions.ForbiddenException("nope")
                    }
                }

                val response = getAuthenticated("dummy/strict")

                assertEquals(HttpStatusCode.InternalServerError, response.status)
            }
        }

    @Test
    fun `strict policy allows a declared thrown status`() =
        runTest {
            testApplication {
                setupApp {
                    handler(api.declaresForbidden) {
                        throw ClientRequestExceptions.ForbiddenException("nope")
                    }
                }

                val response = getAuthenticated("dummy/declares-forbidden")

                assertEquals(HttpStatusCode.Forbidden, response.status)
            }
        }

    @Test
    fun `AllowAnyResponse policy leaves a 404 untouched`() =
        runTest {
            testApplication {
                setupApp { handler(api.allowAll) { null } }

                val response = getAuthenticated("dummy/allow-all")

                assertEquals(HttpStatusCode.NotFound, response.status)
            }
        }
}
