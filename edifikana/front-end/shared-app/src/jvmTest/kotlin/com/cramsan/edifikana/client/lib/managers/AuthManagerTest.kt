package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.UserModel
import com.cramsan.edifikana.client.lib.service.AuthService
import com.cramsan.edifikana.client.lib.service.PropertyService
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.UnifiedDispatcherProvider
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.test.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest

/**
 * Unit tests for the AuthManager class.
 */
class AuthManagerTest : TestBase() {
    private lateinit var dependencies: ManagerDependencies
    private lateinit var propertyService: PropertyService
    private lateinit var authService: AuthService
    private lateinit var manager: AuthManager

    /**
     * Sets up the test environment, initializing mocks and the AuthManager instance.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        propertyService = mockk(relaxed = true)
        authService = mockk(relaxed = true)

        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        manager = AuthManager(dependencies, propertyService, authService)
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
    fun `signIn returns user and sets active property`() = runTest {
        // Arrange
        val user = mockk<UserModel>()
        val propertyId = PropertyId("property-1")
        val propertyList = listOf(mockk<com.cramsan.edifikana.client.lib.models.PropertyModel> {
            every { id } returns propertyId
        })
        coEvery { authService.signIn(any(), any()) } returns Result.success(user)
        coEvery { propertyService.getPropertyList() } returns Result.success(propertyList)
        coEvery { propertyService.setActiveProperty(propertyId) } returns Result.success(Unit)
        // Act
        val result = manager.signIn("email", "password")
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify { authService.signIn("email", "password") }
        coVerify { propertyService.getPropertyList() }
        coVerify { propertyService.setActiveProperty(propertyId) }
    }

    /**
     * Tests signInWithOtp returns user.
     */
    @Test
    fun `signInWithOtp returns user`() = runTest {
        // Arrange
        val user = mockk<UserModel>()
        coEvery { authService.signInWithOtp("email", "token") } returns Result.success(user)
        // Act
        val result = manager.signInWithOtp("email", "token")
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(user, result.getOrNull())
        coVerify { authService.signInWithOtp("email", "token") }
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
}

