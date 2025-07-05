package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.HttpClient
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [AuthServiceImpl].
 * TODO: SKELETON FOR TESTING, NEEDS TO BE UPDATED AS CLASS IS NOT VERY TESTABLE ATM
 */
@Ignore
class AuthServiceImplTest {
    private val auth = mockk<Auth>(relaxed = true)
    private val http = mockk<HttpClient>()
    private val service = AuthServiceImpl(auth, http)

    /**
     * Setup the test environment.
     */
    @BeforeTest
    fun setupTest() {
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
        val response = mockk<HttpResponse> {
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
        val userNetworkResponse = mockk<UserNetworkResponse> {
            coEvery { toUserModel() } returns UserModel(
                UserId("user-3"),
                "email",
                "phone",
                "first",
                "last",
                true,
                )
        }
        coEvery { auth.currentUserOrNull() } returns userInfo
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { http.get(any<String>()) } returns mockk {
//            coEvery { body<UserNetworkResponse>() } returns userNetworkResponse
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
        val email = "garcia.alicia1990@gmail.com"
        coEvery { auth.signInWith(OTP, any()) } returns Unit

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
