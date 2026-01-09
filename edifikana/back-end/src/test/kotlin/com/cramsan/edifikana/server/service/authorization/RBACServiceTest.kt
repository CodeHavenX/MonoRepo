package com.cramsan.edifikana.server.service.authorization

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.datastore.EmployeeDatastore
import com.cramsan.edifikana.server.datastore.EventLogDatastore
import com.cramsan.edifikana.server.datastore.OrganizationDatastore
import com.cramsan.edifikana.server.datastore.PropertyDatastore
import com.cramsan.edifikana.server.datastore.TimeCardDatastore
import com.cramsan.edifikana.server.service.models.Employee
import com.cramsan.edifikana.server.service.models.Property
import com.cramsan.edifikana.server.service.models.User
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.junit.jupiter.params.provider.CsvSource
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.assertEquals

class RBACServiceTest {
    private lateinit var propertyDatastore: PropertyDatastore
    private lateinit var orgDatastore: OrganizationDatastore
    private lateinit var employeeDatastore: EmployeeDatastore
    private lateinit var timeCardDatastore: TimeCardDatastore
    private lateinit var eventLogDatastore: EventLogDatastore
    private lateinit var rbac: RBACService

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance((PassthroughEventLogger(StdOutEventLoggerDelegate())))
        propertyDatastore = mockk()
        orgDatastore = mockk()
        employeeDatastore = mockk()
        timeCardDatastore = mockk()
        eventLogDatastore = mockk()
        rbac = RBACService(propertyDatastore, orgDatastore, employeeDatastore, timeCardDatastore, eventLogDatastore)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that hasRole returns true when the user is performing an action on their own account.
     */
    @Test
    fun `hasRole for a user action returns role for user when action is on own account`() {
        // Arrange
        val userId = UserId("myUserId")
        val targetUserId = UserId("myUserId")
        val targetUser = mockk<User>()
        every { targetUser.id } returns targetUserId

        val context = ClientContext.AuthenticatedClientContext(SupabaseContextPayload(mockk(), userId))

        // Act
        val result = rbac.hasRole(context, targetUser.id)

        // Assert
        assertTrue(result)
    }

    /**
     * Tests that hasRole throws a ForbiddenException when the user is attempting to perform an action on another user's account.
     */
    @Test
    fun `hasRole for user action throws exception when action is for diff user from requester`() {
        // Arrange
        val userId = UserId("myUserId")
        val targetUserId = UserId("anotherUserId")
        val targetUser = mockk<User>()
        every { targetUser.id } returns targetUserId

        val context = ClientContext.AuthenticatedClientContext(SupabaseContextPayload(mockk(), userId))

        // Act & Assert
        assertThrows(ClientRequestExceptions.ForbiddenException::class.java) {
            rbac.hasRole(context, targetUser.id)
        }
    }

    /**
     * Tests that hasRole returns true when the user has the required role for an organization action.
     */
    @Test
    fun `hasRole for org action returns expected role for authorized user`() = runTest {
        // Arrange
        val userId = UserId("testUser")
        val orgId = OrganizationId("testOrg")
        val requiredRole = UserRole.ADMIN

        val context = ClientContext.AuthenticatedClientContext(SupabaseContextPayload(mockk(), userId))

        coEvery {
            orgDatastore.getUserRole(userId, orgId)
        } returns Result.success(requiredRole)

        // Act
        val result = rbac.hasRole(context, orgId, requiredRole)

        // Assert
        assertTrue(result)
    }

    /**
     * Tests that hasRole returns false when the user does not have the required role for an organization action.
     */
    @Test
    fun `hasRole for org action returns false`() = runTest {
        // Arrange
        val userId = UserId("testUser")
        val userRole = UserRole.EMPLOYEE
        val orgId = OrganizationId("testOrg")
        val requiredRole = UserRole.OWNER

        val user = mockk<User>()
        every { user.id } returns userId
        every { user.role } returns UserRole.EMPLOYEE

        val context = ClientContext.AuthenticatedClientContext(SupabaseContextPayload(mockk(), userId))

        coEvery {
            orgDatastore.getUserRole(userId, orgId)
        } returns Result.success(userRole)

        // Act
        val result = rbac.hasRole(context, orgId, requiredRole)

        // Assert
        assertFalse(result)
    }

    /**
     * Tests hasRoleOrHigher for organization actions using parameterized inputs from a CSV file.
     *
     * @param userId The ID of the user making the request.
     * @param userRole The role of the user making the request.
     * @param orgId The ID of the organization on which the action is to be performed
     * @param requiredRole The minimum role required to perform the action.
     * @param expected The expected result of the hasRoleOrHigher check.
     */
    @ParameterizedTest
    @CsvFileSource(resources = ["/authorization/orgHasRoleOrHigherTests.csv"], numLinesToSkip = 1)
    fun `hasRoleOrHigher for organization actions returns expected results for requester`(
        userId: UserId,
        userRole: UserRole,
        orgId: OrganizationId,
        requiredRole: UserRole,
        expected: Boolean,
    ) = runTest {
        // Arrange
        val user = mockk<User>()
        every { user.id } returns userId
        every { user.role } returns userRole

        val context = ClientContext.AuthenticatedClientContext(SupabaseContextPayload(mockk(), userId))
        coEvery {
            orgDatastore.getUserRole(userId, orgId)
        } returns Result.success(userRole)

        // Act & Assert
        val result = rbac.hasRoleOrHigher(context, orgId, requiredRole)
        assertEquals(expected, result)
    }

    /**
     * Tests hasRole for property actions using parameterized inputs from a CSV source.
     *
     * @param userId The ID of the user making the request.
     * @param userRole The role of the user making the request.
     * @param propId The ID of the property on which the action is to be performed
     * @param requiredRole The role required to perform the action.
     * @param expected The expected result of the hasRole check.
     */
    @ParameterizedTest
    @CsvSource(
        "testUserId, OWNER, testProperty, OWNER, true",
        "testUserId2, EMPLOYEE, testProp2, OWNER, false"
    )
    fun `hasRole for property actions returns expected results for requester`(
        userId: UserId,
        userRole: UserRole,
        propId: PropertyId,
        requiredRole: UserRole,
        expected: Boolean,
    ) = runTest {
        // Arrange
        val orgId = OrganizationId("mockOrgId")
        val user = mockk<User>()
        every { user.id } returns userId
        every { user.role } returns userRole
        val property = mockk<Property>()
        every { property.organizationId } returns orgId

        val context = ClientContext.AuthenticatedClientContext(SupabaseContextPayload(mockk(), userId))
        coEvery { propertyDatastore.getProperty(propId) } returns Result.success(property)
        coEvery { orgDatastore.getUserRole(userId, orgId) } returns Result.success(userRole)

        // Act & Assert
        val result = rbac.hasRole(context, propId, requiredRole)
        assertEquals(expected, result)
    }

    /**
     * Tests hasRoleOrHigher for property actions using parameterized inputs from a CSV file.
     *
     * @param userId The ID of the user making the request.
     * @param userRole The role of the user making the request.
     * @param propId The ID of the property on which the action is to be performed
     * @param requiredRole The minimum role required to perform the action.
     * @param expected The expected result of the hasRoleOrHigher check.
     */
    @ParameterizedTest
    @CsvFileSource(resources = ["/authorization/propertyHasRoleOrHigherTests.csv"], numLinesToSkip = 1)
    fun `hasRoleOrHigher for property actions returns expected result for user`(
        userId: UserId,
        userRole: UserRole,
        propId: PropertyId,
        requiredRole: UserRole,
        expected: Boolean,
    ) = runTest {
        // Arrange
        val orgId = OrganizationId("mockOrgId")
        val user = mockk<User>()
        every { user.id } returns userId
        every { user.role } returns userRole

        val property = mockk<Property>()
        every { property.organizationId } returns orgId

        val context = ClientContext.AuthenticatedClientContext(SupabaseContextPayload(mockk(), userId))
        coEvery { propertyDatastore.getProperty(propId) } returns Result.success(property)
        coEvery { orgDatastore.getUserRole(userId, orgId) } returns Result.success(userRole)

        // Act & Assert
        val result = rbac.hasRoleOrHigher(context, propId, requiredRole)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "testUserId, EMPLOYEE, Employee1, EMPLOYEE, true",
        "testUserId2, MANAGER, Employee2, ADMIN, false"
    )
    fun `hasRole for employee actions returns expected results for requester`(
        userId: UserId,
        userRole: UserRole,
        empId: EmployeeId,
        requiredRole: UserRole,
        expected: Boolean,
    ) = runTest {
        // Arrange
        val propId = PropertyId("mockPropId")
        val orgId = OrganizationId("mockOrgId")
        val employee = mockk<Employee>()
        every { employee.id } returns empId
        every { employee.propertyId } returns propId

        val user = mockk<User>()
        every { user.id } returns userId
        every { user.role } returns userRole

        val property = mockk<Property>()
        every { property.organizationId } returns orgId

        val context = ClientContext.AuthenticatedClientContext(SupabaseContextPayload(mockk(), userId))
        coEvery { employeeDatastore.getEmployee(empId) } returns Result.success(employee)
        coEvery { propertyDatastore.getProperty(propId) } returns Result.success(property)
        coEvery { orgDatastore.getUserRole(userId, orgId) } returns Result.success(userRole)

        // Act & Assert
        val result = rbac.hasRole(context, empId, requiredRole)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/authorization/employeeHasRoleOrHigherTests.csv"], numLinesToSkip = 1)
    fun `hasRoleOrHigher for employee actions returns expected result for user`(
        userId: UserId,
        userRole: UserRole,
        empId: EmployeeId,
        requiredRole: UserRole,
        expected: Boolean,
    ) = runTest {
        // Arrange
        val propId = PropertyId("mockPropId")
        val orgId = OrganizationId("mockOrgId")
        val employee = mockk<Employee>()
        every { employee.id } returns empId
        every { employee.propertyId } returns propId

        val user = mockk<User>()
        every { user.id } returns userId
        every { user.role } returns userRole

        val property = mockk<Property>()
        every { property.organizationId } returns orgId

        val context = ClientContext.AuthenticatedClientContext(SupabaseContextPayload(mockk(), userId))
        coEvery { employeeDatastore.getEmployee(empId) } returns Result.success(employee)
        coEvery { propertyDatastore.getProperty(propId) } returns Result.success(property)
        coEvery { orgDatastore.getUserRole(userId, orgId) } returns Result.success(userRole)

        // Act & Assert
        val result = rbac.hasRoleOrHigher(context, empId, requiredRole)
        assertEquals(expected, result)
    }
}
