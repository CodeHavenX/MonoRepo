package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.Organization
import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.OrganizationService
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.CoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the AuthManager class.
 */
class AuthManagerTest : CoroutineTest() {
    private lateinit var dependencies: ManagerDependencies
    private lateinit var authService: AuthService
    private lateinit var manager: AuthManager
    private lateinit var organizationService: OrganizationService

    /**
     * Sets up the test environment, initializing mocks and the AuthManager instance.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        authService = mockk(relaxed = true)
        organizationService = mockk(relaxed = true)

        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        manager = AuthManager(dependencies, authService)
    }

    /**
     * Tests isSignedIn returns the result from authService.
     */
    @Test
    fun `isSignedIn returns result`() = runTest {
        // Arrange
        coEvery { authService.isSignedIn() } returns Result.success(true)
        // Act
        val result = manager.isSignedIn()
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        coVerify { authService.isSignedIn() }
    }

    /**
     * Tests signIn returns user and sets active property if available.
     */
    @Test
    fun `signIn returns user`() = runCoroutineTest {
        // Arrange
        val user = mockk<UserModel>()
        val propertyId = PropertyId("property-1")
        val propertyList = listOf(mockk<com.cramsan.edifikana.client.lib.models.PropertyModel> {
            every { id } returns propertyId
        })
        coEvery { authService.signInWithPassword(any(), any()) } returns Result.success(user)
        // Act
        val result = manager.signInWithPassword("email", "password")
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify { authService.signInWithPassword("email", "password") }
    }

    /**
     * Tests signInWithOtp returns user and sets active property.
     */
    @Test
    fun `signInWithOtp returns user`() = runTest {
        // Arrange
        val user = mockk<UserModel>()
        val propertyId = PropertyId("property-1")
        val propertyList = listOf(mockk<com.cramsan.edifikana.client.lib.models.PropertyModel> {
            every { id } returns propertyId
        })
        coEvery { authService.signInWithOtp("email", "token", createUser = false) } returns Result.success(user)

        // Act
        val result = manager.signInWithOtp("email", "token", createUser = false)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify { authService.signInWithOtp("email", "token", createUser = false) }
    }

    /**
     * Tests signUp returns user.
     */
    @Test
    fun `signUp returns user`() = runTest {
        // Arrange
        val user = mockk<UserModel>()
        coEvery { authService.signUp(any(), any(), any(), any()) } returns Result.success(user)
        // Act
        val result = manager.signUp("email", "phone", "first", "last")
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify { authService.signUp("email", "phone", "first", "last") }
    }

    /**
     * Tests signOut calls authService.
     */
    @Test
    fun `signOut calls authService`() = runTest {
        // Arrange
        coEvery { authService.signOut() } returns Result.success(Unit)
        // Act
        val result = manager.signOut()
        // Assert
        assertTrue(result.isSuccess)
        coVerify { authService.signOut() }
    }

    /**
     * Tests getUser returns user.
     */
    @Test
    fun `getUser returns user`() = runTest {
        // Arrange
        val user = mockk<UserModel>()
        coEvery { authService.getUser() } returns Result.success(user)
        // Act
        val result = manager.getUser()
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify { authService.getUser() }
    }

    /**
     * Tests activeUser returns StateFlow from authService.
     */
    @Test
    fun `activeUser returns StateFlow`() {
        // Arrange
        val stateFlow = MutableStateFlow<UserId?>(UserId("user-1"))
        every { authService.activeUser() } returns stateFlow
        // Act
        val result = manager.activeUser()
        // Assert
        assertEquals(stateFlow, result)
    }

    /**
     * Tests verifyPermissions calls authService.
     */
    @Test
    fun `verifyPermissions calls authService`() = runTest {
        // Arrange
        coEvery { authService.verifyPermissions() } returns Result.success(mockk())
        // Act
        val result = manager.verifyPermissions()
        // Assert
        assertTrue(result.isSuccess)
        coVerify { authService.verifyPermissions() }
    }

    /**
     * Tests that inviteEmployee calls the service and returns success.
     */
    @Test
    fun `inviteEmployee calls service`() = runCoroutineTest {
        // Arrange
        val email = "test@example.com"
        val organizationId = OrganizationId("org-1")
        coEvery { authService.inviteEmployee(email, organizationId) } returns Result.success(Unit)
        // Act
        val result = manager.inviteEmployee(email, organizationId)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { authService.inviteEmployee(email, organizationId) }
    }

    /**
     * Tests checkUserExists returns true when authService reports existence.
     */
    @Test
    fun `checkUserExists returns true when user exists`() = runTest {
        // Arrange
        val email = "exists@example.com"
        coEvery { authService.checkUserExists(email) } returns Result.success(true)

        // Act
        val result = manager.checkUserExists(email)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        coVerify { authService.checkUserExists(email) }
    }

    /**
     * Tests checkUserExists returns failure when authService fails.
     */
    @Test
    fun `checkUserExists returns failure when service errors`() = runTest {
        // Arrange
        val email = "error@example.com"
        val exception = Exception("service error")
        coEvery { authService.checkUserExists(email) } returns Result.failure(exception)

        // Act
        val result = manager.checkUserExists(email)

        // Assert
        assertTrue(result.isFailure)
        coVerify { authService.checkUserExists(email) }
    }

}
