package com.cramsan.edifikana.server.core.service.authorization

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.UserService
import com.cramsan.edifikana.server.core.service.models.User
import com.cramsan.edifikana.server.core.service.models.UserRole
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Test class for [RoleBasedAccessControl].
 */
class RoleBasedAccessControlServiceTest {
    private lateinit var userService: UserService
    private lateinit var rbac: RoleBasedAccessControlService

    /**
     * Sets up the test environment by initializing mocks for [UserService] and [RoleBasedAccessControl].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        userService = mockk()
        rbac = RoleBasedAccessControlService(userService)
    }

    /**
     * Tests that retrieveUserRole returns the correct role when user is authorized with same userId.
     */
    @Test
    fun `retrieveUserRole returns user role for authorized user with same userId`() = runTest {
        // Arrange
        val userId = UserId("myUserId")
        val email = "Directo@test.me" //TODO: PLACEHOLDER UNTIL ORGANIZATION CAN BE USED
        val role = UserRole.MANAGER
        val user = mockk<User>()
        every { user.id } returns userId
        every { user.email } returns email
        every { user.role } returns role

        val targetUserId = UserId("staffId")
        val targetUserEmail = "Directo@test.me"
        val targetUser = mockk<User>()
        every { targetUser.id } returns targetUserId
        every { targetUser.email } returns targetUserEmail
        val context = ClientContext.AuthenticatedClientContext(mockk(), userId)
        coEvery { userService.getUser(userId) } returns Result.success(user)

        // Act
        val result = rbac.retrieveUserRole(context, targetUser)

        // Assert
        assertEquals(role, result   )
    }

    /**
     * Tests that retrieveUserRole returns UNAUTHORIZED role when user is not authorized for target User.
     */
    @Test
    fun `retrieveUserRole returns UNAUTHORIZED role for non-matching Organization`() = runTest {
        // Arrange
        val userId = UserId("myUserId")
        val email = "Director@test.me" //TODO: PLACEHOLDER UNTIL ORGANIZATION CAN BE USED
        val role = UserRole.MANAGER
        val user = mockk<User>()
        every { user.id } returns userId
        every { user.email } returns email
        every { user.role } returns role

        val targetUserId = UserId("staffId")
        val targetUserEmail = "Metropoli@test.me"
        val targetUser = mockk<User>()
        every { targetUser.id } returns targetUserId
        every { targetUser.email } returns targetUserEmail
        val context = ClientContext.AuthenticatedClientContext(mockk(), userId)
        coEvery { userService.getUser(userId) } returns Result.success(user)

        // Act
        val result = rbac.retrieveUserRole(context, targetUser)

        // Assert
        assertEquals(UserRole.UNAUTHORIZED, result   )
    }

    /**
     * Tests that retrieveUserRole returns the correct role when user is authorized for Property.
     */
    @Test
    fun `retrieveUserRole returns user role for authorized Property in matching organization`() = runTest {
        // Arrange
        val userId = UserId("validId")
        val firstName = "Metropoli" //TODO: PLACEHOLDER UNTIL ORGANIZATION CAN BE USED
        val role = UserRole.OWNER
        val user = mockk<User>()
        every { user.id } returns userId
        every { user.firstName } returns firstName
        every { user.role } returns role

        val propertyName = "Metropoli"
        val property = mockk<Property>()
        every { property.name } returns propertyName

        val context = ClientContext.AuthenticatedClientContext(mockk(), userId)
        coEvery { userService.getUser(userId) } returns Result.success(user)

        // Act
        val result = rbac.retrieveUserRole(context, property)

        // Assert
        assertEquals(role, result)
    }

    /**
     * Tests that retrieveUserRole returns UNAUTHORIZED when user is not authorized for Property.
     */
    @Test
    fun `retrieveUserRole returns UNAUTHORIZED for non-matching Property organization`() = runTest {
        // Arrange
        val userId = UserId("validId")
        val firstName = "Cenit" //TODO: PLACEHOLDER UNTIL ORGANIZATION CAN BE USED
        val role = UserRole.OWNER
        val user = mockk<User>()
        every { user.id } returns userId
        every { user.firstName } returns firstName
        every { user.role } returns role

        val propertyName = "Metropoli"
        val property = mockk<Property>()
        every { property.name } returns propertyName
        every { property.id } returns mockk()

        val context = ClientContext.AuthenticatedClientContext(mockk(), userId)
        coEvery { userService.getUser(userId) } returns Result.success(user)

        // Act
        val result = rbac.retrieveUserRole(context, property)

        // Assert
        assertEquals(UserRole.UNAUTHORIZED, result)
    }

    /**
     * Tests that retrieveUserRole returns the correct role when user is authorized for Staff.
     */
    @Test
    fun `retrieveUserRole returns user role for requester when the staff have matching organizations`() = runTest {
        // Arrange
        val userId = UserId("validId")
        val firstName = "Metropoli" //TODO: PLACEHOLDER UNTIL ORGANIZATION CAN BE USED
        val role = UserRole.OWNER
        val user = mockk<User>()
        every { user.id } returns userId
        every { user.firstName } returns firstName
        every { user.role } returns role

        val staffPropertyId = PropertyId("Metropoli")
        val staff = mockk<Staff>()
        every { staff.propertyId } returns staffPropertyId

        val context = ClientContext.AuthenticatedClientContext(mockk(), userId)
        coEvery { userService.getUser(userId) } returns Result.success(user)

        // Act
        val result = rbac.retrieveUserRole(context, staff)

        // Assert
        assertEquals(role, result)
    }

    /**
     * Tests that retrieveUserRole returns UNAUTHORIZED when user is not authorized for Staff.
     */
    @Test
    fun `retrieveUserRole returns UNAUTHORIZED for non-matching organization`() = runTest {
        // Arrange
        val userId = UserId("validId")
        val firstName = "Cenit" //TODO: PLACEHOLDER UNTIL ORGANIZATION CAN BE USED
        val role = UserRole.OWNER
        val user = mockk<User>()
        every { user.id } returns userId
        every { user.firstName } returns firstName
        every { user.role } returns role

        val staffPropertyId = PropertyId("Metropoli")
        val staff = mockk<Staff>()
        every { staff.propertyId } returns staffPropertyId

        val context = ClientContext.AuthenticatedClientContext(mockk(), userId)
        coEvery { userService.getUser(userId) } returns Result.success(user)

        // Act
        val result = rbac.retrieveUserRole(context, staff)

        // Assert
        assertEquals(UserRole.UNAUTHORIZED, result)
    }
}

