package com.cramsan.edifikana.server.controller.authentication

import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.lib.serialization.HEADER_TOKEN_AUTH
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.http.Headers
import io.ktor.http.headersOf
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.ApplicationRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Unit tests for [SupabaseContextRetriever], focused on distinguishing an unauthenticated client
 * (missing/rejected token) from an auth-provider outage (which must not be reported as a 401).
 */
class SupabaseContextRetrieverTest {
    private lateinit var auth: Auth
    private lateinit var contextRetriever: SupabaseContextRetriever

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        AssertUtil.setInstance(NoopAssertUtil())
        auth = mockk()
        contextRetriever = SupabaseContextRetriever(auth)
    }

    private fun makeApplicationCall(headerValue: String?): ApplicationCall {
        val request = mockk<ApplicationRequest>()
        val headers = if (headerValue == null) Headers.Empty else headersOf(HEADER_TOKEN_AUTH, headerValue)
        every { request.headers } returns headers

        val applicationCall = mockk<ApplicationCall>()
        every { applicationCall.request } returns request
        return applicationCall
    }

    @Test
    fun `returns unauthenticated when no token header is present`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = null)

            val result = contextRetriever.getContext(applicationCall)

            assertTrue(result is ClientContext.UnauthenticatedClientContext)
            coVerify(exactly = 0) { auth.retrieveUser(any()) }
        }

    @Test
    fun `returns unauthenticated when token header is blank`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "   ")

            val result = contextRetriever.getContext(applicationCall)

            assertTrue(result is ClientContext.UnauthenticatedClientContext)
            coVerify(exactly = 0) { auth.retrieveUser(any()) }
        }

    @Test
    fun `returns unauthenticated when Supabase rejects the token`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "invalid-token")
            coEvery { auth.retrieveUser(any()) } throws mockk<RestException>(relaxed = true)

            val result = contextRetriever.getContext(applicationCall)

            assertTrue(result is ClientContext.UnauthenticatedClientContext)
        }

    @Test
    fun `propagates the failure when Supabase is unreachable`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "some-token")
            coEvery { auth.retrieveUser(any()) } throws mockk<HttpRequestException>(relaxed = true)

            // A transport failure must NOT be swallowed into an unauthenticated context; it propagates
            // so the handler surfaces a 5xx instead of a misleading 401.
            assertFailsWith<HttpRequestException> {
                contextRetriever.getContext(applicationCall)
            }
        }

    @Test
    fun `returns authenticated context for a valid token`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "valid-token")
            val userInfo = mockk<UserInfo>(relaxed = true).also { every { it.id } returns "user-1" }
            coEvery { auth.retrieveUser(any()) } returns userInfo
            every { auth.currentUserOrNull() } returns null

            val result = contextRetriever.getContext(applicationCall)

            assertEquals(
                ClientContext.AuthenticatedClientContext(
                    SupabaseContextPayload(userInfo = userInfo, userId = UserId("user-1")),
                ),
                result,
            )
            coVerify { auth.retrieveUser("valid-token") }
        }
}
