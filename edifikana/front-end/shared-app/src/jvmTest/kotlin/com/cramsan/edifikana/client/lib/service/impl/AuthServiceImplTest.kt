package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [AuthServiceImpl].
 */
class AuthServiceImplTest {
    private lateinit var auth: Auth
    private lateinit var ktorTestEngine: KtorTestEngine
    private lateinit var http: HttpClient
    private lateinit var service: AuthServiceImpl
    private lateinit var json: Json

    /**
     * Setup the test environment.
     */
    @BeforeTest
    fun setupTest() {
        auth = mockk<Auth>()
        ktorTestEngine = KtorTestEngine()
        json = createJson()
        http = HttpClient(ktorTestEngine.engine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        service = AuthServiceImpl(auth, http)

        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    /**
     * Tests that isSignedIn returns true when user is present and session refresh succeeds.
     */
    @Test
    fun `isSignedIn returns true when user is present and session refresh succeeds`() = runTest {
        // Arrange
        val userInfo = mockk<UserInfo> { coEvery { id } returns "user-1" }
        coEvery { auth.awaitInitialization() } returns Unit
        coEvery { auth.currentUserOrNull() } returns userInfo
        coEvery { auth.refreshCurrentSession() } returns Unit

        //Act
        val result = service.isSignedIn()

        // Assert
        assertTrue(result.getOrThrow())
        assertEquals(UserId("user-1"), service.activeUser().first())
    }

    /**
     * Tests that isSignedIn returns false when user is not present.
     */
    @Test
    fun `isSignedIn returns false when user is not present`() = runTest {
        // Arrange
        coEvery { auth.awaitInitialization() } returns Unit
        coEvery { auth.currentUserOrNull() } returns null

        //Act
        val result = service.isSignedIn()

        // Assert
        assertFalse(result.getOrThrow())
        assertEquals(null, service.activeUser().first())
    }

    /**
     * Tests that isSignedIn returns false when session refresh fails.
     */
    @Test
    fun `isSignedIn returns false when session refresh fails`() = runTest {
        // Arrange
        val userInfo = mockk<UserInfo> { coEvery { id } returns "user-2" }
        coEvery { auth.awaitInitialization() } returns Unit
        coEvery { auth.currentUserOrNull() } returns userInfo
        val response = mockk<HttpResponse>(relaxed = true) {
            coEvery { status } returns HttpStatusCode.Unauthorized
        }
        coEvery { auth.refreshCurrentSession() } throws RestException("fail", "401 Error", response )

        //Act
        val result = service.isSignedIn()

        // Assert
        assertFalse(result.getOrThrow())
        assertEquals(UserId("user-2"), service.activeUser().first())
    }

    /**
     * Tests that getUser returns mapped user when signed in.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getUser returns mapped user when signed in`() = runTest {
        // Arrange
        val userInfo = mockk<UserInfo> { coEvery { id } returns "user-3" }
        val userNetworkResponse = UserNetworkResponse(
            id = "user-3",
            email = "email",
            phoneNumber = "phone",
            firstName = "first",
            lastName = "last",
            authMetadata = null,
        )
        coEvery { auth.currentUserOrNull() } returns userInfo

        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(userNetworkResponse)
            )
        }

        //Act
        val result = service.getUser()

        // Assert
        assertEquals(UserId("user-3"), result.getOrThrow().id)
        assertEquals(UserId("user-3"), service.activeUser().first())
    }

    /**
     * Tests that getUser throws error when not signed in.
     */
    @Test
    fun `getUser throws error when not signed in`() = runTest {
        // Arrange
        coEvery { auth.currentUserOrNull() } returns null

        //Act
        val result = service.getUser()

        // Assert
        assertTrue(result.isFailure)
    }

    /**
     * Tests that sendOtpEmail returns success when auth signInWith succeeds.
     */
    @Test
    fun `sendOtpEmail returns success when auth signInWith succeeds`() = runTest {
        // Arrange
        val email = "test@gmail.com"
        coEvery { auth.signInWith(any<OTP>(), anyNullable(), any<(OTP.Config.() -> Unit)>()) } just Runs
        coEvery { auth.config } returns mockk { every { defaultRedirectUrl } returns "" }

        // Act
        val result = service.sendOtpEmail(email)

        // Assert
        assertTrue(result.isSuccess)
    }

    /**
     * Tests that sendOtpEmail returns failure when auth signInWith throws.
     */
    @Test
    fun `sendOtpEmail returns failure when auth signInWith throws`() = runTest {
        // Arrange
        val email = "test@example.com"
        coEvery { auth.signInWith(OTP, any()) } throws RuntimeException("fail")

        // Act
        val result = service.sendOtpEmail(email)

        // Assert
        assertTrue(result.isFailure)
    }
}
