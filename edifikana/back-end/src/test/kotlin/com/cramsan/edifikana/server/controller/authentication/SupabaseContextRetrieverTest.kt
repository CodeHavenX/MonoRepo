package com.cramsan.edifikana.server.controller.authentication

import com.cramsan.edifikana.lib.model.user.UserId
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
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Unit tests for [SupabaseContextRetriever], focused on distinguishing a rejected token (401) from an
 * auth-provider outage (which must not be reported as a 401).
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

    @Test
    fun `returns unauthenticated when Supabase rejects the token`() =
        runTest {
            coEvery { auth.retrieveUser(any()) } throws mockk<RestException>(relaxed = true)

            val result = contextRetriever.getContext("invalid-token")

            assertTrue(result is ClientContext.UnauthenticatedClientContext)
        }

    @Test
    fun `propagates the failure when Supabase is unreachable`() =
        runTest {
            coEvery { auth.retrieveUser(any()) } throws mockk<HttpRequestException>(relaxed = true)

            // A transport failure must NOT be swallowed into an unauthenticated context; it propagates
            // so the handler surfaces a 5xx instead of a misleading 401.
            assertFailsWith<HttpRequestException> {
                contextRetriever.getContext("some-token")
            }
        }

    @Test
    fun `returns authenticated context for a valid token`() =
        runTest {
            val userInfo = mockk<UserInfo>(relaxed = true).also { every { it.id } returns "user-1" }
            coEvery { auth.retrieveUser(any()) } returns userInfo
            every { auth.currentUserOrNull() } returns null

            val result = contextRetriever.getContext("valid-token")

            assertEquals(
                ClientContext.AuthenticatedClientContext(
                    SupabaseContextPayload(userInfo = userInfo, userId = UserId("user-1")),
                ),
                result,
            )
        }
}
