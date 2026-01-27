package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.datastore.EmployeeDatastore
import com.cramsan.edifikana.server.service.models.Employee
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest

/**
 * Test class for [EmployeeService].
 */
class EmployeeServiceTest {
    private lateinit var employeeDatastore: EmployeeDatastore
    private lateinit var employeeService: EmployeeService

    /**
     * Sets up the test environment by initializing mocks for [EmployeeDatastore] and [employeeService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        employeeDatastore = mockk()
        employeeService = EmployeeService(employeeDatastore)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that createEmployee creates an employee and returns it.
     */
    @Test
    fun `createEmployee should call employeeDatastore and return employee`() = runTest {
        // Arrange
        val employee = mockk<Employee>()
        val idType = IdType.DNI
        val firstName = "John"
        val lastName = "Doe"
        val role = EmployeeRole.MANAGER
        val propertyId = PropertyId("property-1")
        coEvery {
            employeeDatastore.createEmployee(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.success(employee)

        // Act
        val result = employeeService.createEmployee(idType, firstName, lastName, role, propertyId)

        // Assert
        assertEquals(employee, result)
        coVerify {
            employeeDatastore.createEmployee(
                idType,
                firstName,
                lastName,
                role,
                propertyId,
            )
        }
    }

    /**
     * Tests that getEmployee retrieves an employee by ID and returns it.
     */
    @Test
    fun `getEmployee should call employeeDatastore and return employee`() = runTest {
        // Arrange
        val employee = mockk<Employee>()
        val employeeId = EmployeeId("employee-1")
        coEvery { employeeDatastore.getEmployee(any()) } returns Result.success(employee)

        // Act
        val result = employeeService.getEmployee(employeeId)

        // Assert
        assertEquals(employee, result)
        coVerify { employeeDatastore.getEmployee(employeeId) }
    }

    /**
     * Tests that getEmployee returns null if the employee is not found.
     */
    @Test
    fun `getEmployee should return null if not found`() = runTest {
        // Arrange
        val employeeId = EmployeeId("employee-2")
        coEvery { employeeDatastore.getEmployee(any()) } returns Result.failure(Exception("Not found"))

        // Act
        val result = employeeService.getEmployee(employeeId)

        // Assert
        assertNull(result)
        coVerify { employeeDatastore.getEmployee(employeeId) }
    }

    /**
     * Tests that getEmployees retrieves all employees and returns a list.
     */
    @Test
    fun `getEmployees should call employeeDatastore and return list`() = runTest {
        // Arrange
        val employeeLists = listOf(mockk<Employee>(), mockk<Employee>())
        val request = UserId("user-1")
        val clientContext = ClientContext.AuthenticatedClientContext<SupabaseContextPayload>(
            SupabaseContextPayload(
                userInfo = mockk(),
                userId = UserId("user-1"),
            ),
        )
        coEvery { employeeDatastore.getEmployees(request) } returns Result.success(employeeLists)

        // Act
        val result = employeeService.getEmployees(clientContext)

        // Assert
        assertEquals(employeeLists, result)
        coVerify { employeeDatastore.getEmployees(request) }
    }

    /**
     * Tests that updateEmployee updates an employee and returns the updated employee.
     */
    @Test
    fun `updateEmployee should call employeeDatastore and return updated employee`() = runTest {
        // Arrange
        val employee = mockk<Employee>()
        val employeeId = EmployeeId("employee-3")
        val idType = IdType.DNI
        val firstName = "Jane"
        val lastName = "Smith"
        val role = EmployeeRole.MANAGER
        coEvery {
            employeeDatastore.updateEmployee(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.success(employee)

        // Act
        val result = employeeService.updateEmployee(employeeId, idType, firstName, lastName, role)

        // Assert
        assertEquals(employee, result)
        coVerify {
            employeeDatastore.updateEmployee(
                employeeId,
                idType,
                firstName,
                lastName,
                role,
            )
        }
    }

    /**
     * Tests that deleteEmployee deletes a employee and returns true.
     */
    @Test
    fun `deleteEmployee should call employeeDatastore and return true`() = runTest {
        // Arrange
        val employeeId = EmployeeId("employee-4")
        coEvery { employeeDatastore.deleteEmployee(any()) } returns Result.success(true)

        // Act
        val result = employeeService.deleteEmployee(employeeId)

        // Assert
        assertEquals(true, result)
        coVerify { employeeDatastore.deleteEmployee(employeeId) }
    }
}
