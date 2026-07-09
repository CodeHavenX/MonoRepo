package com.cramsan.flyerboard.server.controller.authentication

import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.lib.serialization.HEADER_TOKEN_AUTH
import com.cramsan.flyerboard.server.datastore.UserProfileDatastore
import com.cramsan.flyerboard.server.service.models.UserProfile
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Unit tests for [FlyerBoardContextRetriever].
 */
@OptIn(ExperimentalTime::class)
class FlyerBoardContextRetrieverTest {
    private lateinit var auth: Auth
    private lateinit var userProfileDatastore: UserProfileDatastore
    private lateinit var contextRetriever: FlyerBoardContextRetriever

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        auth = mockk()
        userProfileDatastore = mockk()
        contextRetriever = FlyerBoardContextRetriever(auth, userProfileDatastore)
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun makeApplicationCall(headerValue: String?): ApplicationCall {
        val request = mockk<ApplicationRequest>()
        val headers = if (headerValue == null) Headers.Empty else headersOf(HEADER_TOKEN_AUTH, headerValue)
        every { request.headers } returns headers

        val applicationCall = mockk<ApplicationCall>()
        every { applicationCall.request } returns request
        return applicationCall
    }

    private fun makeUserInfo(userId: String = "user-1") =
        mockk<UserInfo>(relaxed = true).also {
            every { it.id } returns userId
        }

    private fun makeProfile(
        userId: String = "user-1",
        role: UserRole = UserRole.USER,
    ) = UserProfile(
        id = UserId(userId),
        role = role,
        createdAt = Instant.fromEpochSeconds(0),
        updatedAt = Instant.fromEpochSeconds(0),
    )

    // ── getContext ────────────────────────────────────────────────────────────

    @Test
    fun `getContext returns unauthenticated when no Authorization header`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = null)

            val result = contextRetriever.getContext(applicationCall)

            assertTrue(result is ClientContext.UnauthenticatedClientContext)
            assertEquals(ClientContext.UnauthenticatedClientContext<FlyerBoardContextPayload>(), result)
        }

    @Test
    fun `getContext returns unauthenticated when Supabase rejects the token`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "Bearer sometoken")
            coEvery { auth.retrieveUser(any()) } throws mockk<RestException>(relaxed = true)

            val result = contextRetriever.getContext(applicationCall)

            assertTrue(result is ClientContext.UnauthenticatedClientContext)
            coVerify(exactly = 0) { userProfileDatastore.getUserProfile(any()) }
        }

    @Test
    fun `getContext propagates the failure when Supabase is unreachable`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "Bearer sometoken")
            coEvery { auth.retrieveUser(any()) } throws mockk<HttpRequestException>(relaxed = true)

            // A transport failure must NOT be swallowed into an unauthenticated context; it propagates
            // so the handler surfaces a 5xx instead of a misleading 401.
            assertFailsWith<HttpRequestException> {
                contextRetriever.getContext(applicationCall)
            }
            coVerify(exactly = 0) { userProfileDatastore.getUserProfile(any()) }
        }

    @Test
    fun `getContext returns authenticated context with USER role for existing profile`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "Bearer sometoken")
            val userInfo = makeUserInfo(userId = "user-1")
            val profile = makeProfile(userId = "user-1", role = UserRole.USER)

            coEvery { auth.retrieveUser(any()) } returns userInfo
            coEvery { userProfileDatastore.getUserProfile(UserId("user-1")) } returns Result.success(profile)

            val result = contextRetriever.getContext(applicationCall)

            assertEquals(
                ClientContext.AuthenticatedClientContext(
                    FlyerBoardContextPayload(UserId("user-1"), UserRole.USER),
                ),
                result,
            )
            coVerify(exactly = 0) { userProfileDatastore.createUserProfile(any(), any()) }
        }

    @Test
    fun `getContext returns authenticated context with ADMIN role for existing profile`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "Bearer sometoken")
            val userInfo = makeUserInfo(userId = "admin-1")
            val profile = makeProfile(userId = "admin-1", role = UserRole.ADMIN)

            coEvery { auth.retrieveUser(any()) } returns userInfo
            coEvery { userProfileDatastore.getUserProfile(UserId("admin-1")) } returns Result.success(profile)

            val result = contextRetriever.getContext(applicationCall)

            assertEquals(
                ClientContext.AuthenticatedClientContext(
                    FlyerBoardContextPayload(UserId("admin-1"), UserRole.ADMIN),
                ),
                result,
            )
            coVerify(exactly = 0) { userProfileDatastore.createUserProfile(any(), any()) }
        }

    @Test
    fun `getContext does not auto-creates profile`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "Bearer sometoken")
            val userInfo = makeUserInfo(userId = "new-user")

            coEvery { auth.retrieveUser(any()) } returns userInfo
            coEvery { userProfileDatastore.getUserProfile(UserId("new-user")) } returns Result.success(null)

            val result = contextRetriever.getContext(applicationCall)

            assertEquals(
                ClientContext.AuthenticatedClientContext(
                    FlyerBoardContextPayload(UserId("new-user"), UserRole.USER),
                ),
                result,
            )
            coVerify(exactly = 0) { userProfileDatastore.createUserProfile(any(), any()) }
        }

    @Test
    fun `getContext propagates the failure when getUserProfile fails`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "Bearer sometoken")
            val userInfo = makeUserInfo(userId = "user-1")

            coEvery { auth.retrieveUser(any()) } returns userInfo
            coEvery { userProfileDatastore.getUserProfile(UserId("user-1")) } returns
                Result.failure(RuntimeException("db error"))

            // A datastore failure is a server-side problem, not an auth failure: it must propagate so the
            // handler surfaces a 5xx instead of a misleading 401.
            assertFailsWith<RuntimeException> {
                contextRetriever.getContext(applicationCall)
            }
        }

    @Test
    fun `getContext strips Bearer prefix before validating token`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "Bearer sometoken")
            val userInfo = makeUserInfo(userId = "user-1")
            val profile = makeProfile(userId = "user-1", role = UserRole.USER)

            coEvery { auth.retrieveUser(any()) } returns userInfo
            coEvery { userProfileDatastore.getUserProfile(UserId("user-1")) } returns Result.success(profile)

            contextRetriever.getContext(applicationCall)

            coVerify { auth.retrieveUser("sometoken") }
        }

    @Test
    fun `getContext returns unauthenticated when Authorization header is blank`() =
        runTest {
            val applicationCall = makeApplicationCall(headerValue = "   ")

            val result = contextRetriever.getContext(applicationCall)

            assertTrue(result is ClientContext.UnauthenticatedClientContext)
            coVerify(exactly = 0) { auth.retrieveUser(any()) }
        }
}
