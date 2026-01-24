package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.lib.model.UserRole
import com.cramsan.edifikana.lib.model.network.CheckUserNetworkResponse
import com.cramsan.edifikana.lib.model.network.InviteListNetworkResponse
import com.cramsan.edifikana.lib.model.network.InviteNetworkResponse
import com.cramsan.edifikana.lib.model.network.UserListNetworkResponse
import com.cramsan.edifikana.lib.model.network.UserNetworkResponse
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
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
        AssertUtil.setInstance(NoopAssertUtil())
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

    /**
     * Tests that checkUserExists returns true when the backend reports the user is registered.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `checkUserExists returns true when backend reports registered`() = runTest {
        // Arrange
        val email = "exists@example.com"
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(CheckUserNetworkResponse(isUserRegistered = true))
            )
        }

        // Act
        val result = service.checkUserExists(email)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
    }

    /**
     * Tests that checkUserExists returns false when the backend reports the user is not registered.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `checkUserExists returns false when backend reports not registered`() = runTest {
        // Arrange
        val email = "notfound@example.com"
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(CheckUserNetworkResponse(isUserRegistered = false))
            )
        }

        // Act
        val result = service.checkUserExists(email)

        // Assert
        assertTrue(result.isSuccess)
        assertFalse(result.getOrThrow())
    }

    /**
     * Test that getUsersByOrganization returns a list of users for the organization.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getUsersByOrganization returns list of users`() = runTest {
        // Arrange
        val userNetworkResponse1 = UserNetworkResponse(
            id = "user-1",
            email = "email1",
            phoneNumber = "phone1",
            firstName = "first1",
            lastName = "last1",
            authMetadata = null,
        )
        val userNetworkResponse2 = UserNetworkResponse(
            id = "user-2",
            email = "email2",
            phoneNumber = "phone2",
            firstName = "first2",
            lastName = "last2",
            authMetadata = null,
        )
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(UserListNetworkResponse(listOf(
                    userNetworkResponse1,
                    userNetworkResponse2,
                ))
            ))
        }

        // Act
        val result = service.getUsersByOrganization(OrganizationId("org-1"))

        // Assert
        assertTrue(result.isSuccess)
        val users = result.getOrThrow()
        assertEquals(2, users.size)
        assertEquals(UserId("user-1"), users[0].id)
        assertEquals(UserId("user-2"), users[1].id)
    }

    /**
     * Tests that signInWithPassword returns user model when credentials are valid.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `signInWithPassword returns user model when credentials are valid`() = runTest {
        val email = "valid@example.com"
        val password = "password123"
        val userInfo = mockk<UserInfo> { coEvery { id } returns "user-6" }
        val userNetworkResponse = UserNetworkResponse(
            id = "user-6",
            email = email,
            phoneNumber = "1234567890",
            firstName = "John",
            lastName = "Doe",
            authMetadata = null,
        )
        coEvery { auth.signInWith(any<Email>(), any(), any()) } just Runs
        coEvery { auth.currentUserOrNull() } returns userInfo
        coEvery { auth.config } returns mockk { every { defaultRedirectUrl } returns "" }
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(userNetworkResponse)
            )
        }

        val result = service.signInWithPassword(email, password)

        assertTrue(result.isSuccess)
        assertEquals(UserId("user-6"), result.getOrThrow().id)
    }

    /**
     * Tests that signOut clears the active user.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `signOut clears active user`() = runTest {
        coEvery { auth.signOut() } just Runs

        val result = service.signOut()

        assertTrue(result.isSuccess)
        assertEquals(null, service.activeUser().first())
    }

    /**
     * Tests that signUp creates a new user and returns the user model.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `signUp creates new user and returns user model`() = runTest {
        val email = "newuser@example.com"
        val phoneNumber = "9876543210"
        val firstName = "Jane"
        val lastName = "Smith"
        val userNetworkResponse = UserNetworkResponse(
            id = "user-7",
            email = email,
            phoneNumber = phoneNumber,
            firstName = firstName,
            lastName = lastName,
            authMetadata = null,
        )
        coEvery { auth.signInWith(any<OTP>(), any(), any()) } just Runs
        coEvery { auth.config } returns mockk { every { defaultRedirectUrl } returns "" }
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(userNetworkResponse)
            )
        }

        val result = service.signUp(email, phoneNumber, firstName, lastName)

        assertTrue(result.isSuccess)
        assertEquals(UserId("user-7"), result.getOrThrow().id)
    }

    /**
     * Tests that signInWithOtp returns user model when OTP verification succeeds and user is created.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `signInWithOtp returns user model when OTP verification succeeds and user is created`() = runTest {
        val email = "user@example.com"
        val hashToken = "validToken"
        val userInfo = mockk<UserInfo> { coEvery { id } returns "user-8" }
        val userNetworkResponse = UserNetworkResponse(
            id = "user-8",
            email = email,
            phoneNumber = "1234567890",
            firstName = "John",
            lastName = "Doe",
            authMetadata = null,
        )
        coEvery { auth.verifyEmailOtp(OtpType.Email.EMAIL, email, hashToken) } just Runs
        coEvery { auth.currentUserOrNull() } returns userInfo
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(userNetworkResponse)
            )
        }

        val result = service.signInWithOtp(email, hashToken, createUser = true)

        assertTrue(result.isSuccess)
        assertEquals(UserId("user-8"), result.getOrThrow().id)
    }

    /**
     * Tests that signInWithOtp returns user model when OTP verification succeeds and user is not created.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `signInWithOtp returns user model when OTP verification succeeds and user is not created`() = runTest {
        val email = "user@example.com"
        val hashToken = "validToken"
        val userInfo = mockk<UserInfo> { coEvery { id } returns "user-9" }
        val userNetworkResponse = UserNetworkResponse(
            id = "user-9",
            email = email,
            phoneNumber = "9876543210",
            firstName = "Jane",
            lastName = "Smith",
            authMetadata = null,
        )
        coEvery { auth.verifyEmailOtp(OtpType.Email.EMAIL, email, hashToken) } just Runs
        coEvery { auth.currentUserOrNull() } returns userInfo
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(userNetworkResponse)
            )
        }

        val result = service.signInWithOtp(email, hashToken, createUser = false)

        assertTrue(result.isSuccess)
        assertEquals(UserId("user-9"), result.getOrThrow().id)
    }

    /**
     * Tests that verifyPermissions returns true when the user does not have admin permissions.
     */
    @Test
    fun `verifyPermissions returns true when user does not have admin permissions`() = runTest {
        // Arrange
        coEvery { auth.admin.retrieveUsers() } throws mockk<AuthRestException>()

        // Act
        val result = service.verifyPermissions()

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
    }

    /**
     * Tests that verifyPermissions returns false when the user has admin permissions.
     */
    @Test
    fun `verifyPermissions returns false when user has admin permissions`() = runTest {
        // Arrange
        coEvery { auth.admin.retrieveUsers() } returns emptyList()

        // Act
        val result = service.verifyPermissions()

        // Assert
        assertTrue(result.isSuccess)
        assertFalse(result.getOrThrow())
    }

    /**
     * Tests that checkUserExists returns true when the backend reports the user is registered.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `checkUserExists returns true when user is registered`() = runTest {
        // Arrange
        val email = "registered@example.com"
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(CheckUserNetworkResponse(isUserRegistered = true))
            )
        }

        // Act
        val result = service.checkUserExists(email)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow())
    }

    /**
     * Tests that checkUserExists returns false when the backend reports the user is not registered.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `checkUserExists returns false when user is not registered`() = runTest {
        // Arrange
        val email = "notregistered@example.com"
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(CheckUserNetworkResponse(isUserRegistered = false))
            )
        }

        // Act
        val result = service.checkUserExists(email)

        // Assert
        assertTrue(result.isSuccess)
        assertFalse(result.getOrThrow())
    }


    /**
     * Verifies that changePassword updates the password successfully.
     */
    @OptIn(SecureStringAccess::class)
    @Test
    fun `changePassword updates password successfully`() = runTest {
        // Arrange
        val currentPassword = SecureString("oldPassword")
        val newPassword = SecureString("newPassword")
        val email = "user@example.com"
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success("")
        }

        // Act
        val result = service.changePassword(
            email,
            currentPassword,
            newPassword
        )

        // Assert
        assertTrue(result.isSuccess)
    }

    /**
     * Verifies that inviteEmployee sends an invite successfully.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `inviteEmployee sends invite successfully`() = runTest {
        // Arrange
        val email = "invite@example.com"
        val organizationId = OrganizationId("org-123")
        val role = UserRole.EMPLOYEE
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success("")
        }

        // Act
        val result = service.inviteEmployee(email, organizationId, role)

        // Assert
        assertTrue(result.isSuccess)
    }

    /**
     * Verifies that getInvites returns a list of invites for the organization.
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getInvites returns list of invites`() = runTest {
        // Arrange
        val organizationId = OrganizationId("org-456")
        val inviteResponse1 = InviteNetworkResponse(
            inviteId = InviteId("invite-1"),
            email = "user1@example.com",
            organizationId = organizationId,
            role = "EMPLOYEE",
            expiresAt = 1234567890L,
        )
        val inviteResponse2 = InviteNetworkResponse(
            inviteId = InviteId("invite-2"),
            email = "user2@example.com",
            organizationId = organizationId,
            role = "MANAGER",
            expiresAt = 1234567891L,
        )
        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(InviteListNetworkResponse(listOf(inviteResponse1, inviteResponse2)))
            )
        }

        // Act
        val result = service.getInvites(organizationId)

        // Assert
        assertTrue(result.isSuccess)
        val invites = result.getOrThrow()
        assertEquals(2, invites.size)
        assertEquals(InviteId("invite-1"), invites[0].id)
        assertEquals("user1@example.com", invites[0].email)
        assertEquals(InviteId("invite-2"), invites[1].id)
        assertEquals("user2@example.com", invites[1].email)
    }
}
