package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.EmployeeModel
import com.cramsan.edifikana.client.lib.service.EmployeeService
import com.cramsan.edifikana.lib.model.EmployeeId
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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for the EmployeeManager class.
 */
class EmployeeManagerTest : CoroutineTest() {
    private lateinit var employeeService: EmployeeService
    private lateinit var dependencies: ManagerDependencies
    private lateinit var manager: EmployeeManager

    /**
     * Sets up the test environment before each test.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        employeeService = mockk()
        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        manager = EmployeeManager(employeeService, dependencies)
    }

    /**
     * Tests that getEmployeeList returns the expected list from the service.
     */
    @Test
    fun `getEmployeeList returns employee list`() = runCoroutineTest {
        // Arrange
        val empList = listOf(mockk<EmployeeModel>(), mockk<EmployeeModel>())
        coEvery { employeeService.getEmployeeList() } returns Result.success(empList)
        // Act
        val result = manager.getEmployeeList()
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(empList, result.getOrNull())
        coVerify { employeeService.getEmployeeList() }
    }

    /**
     * Tests that getEmployee returns the expected employee from the service.
     */
    @Test
    fun `getEmployee returns employee`() = runCoroutineTest {
        // Arrange
        val employeeId = EmployeeId("employee-1")
        val employee = mockk<EmployeeModel>()
        coEvery { employeeService.getEmployee(employeeId) } returns Result.success(employee)
        // Act
        val result = manager.getEmployee(employeeId)
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(employee, result.getOrNull())
        coVerify { employeeService.getEmployee(employeeId) }
    }

    /**
     * Tests that addEmployee calls the service and returns success.
     */
    @Test
    fun `addEmployee calls service`() = runCoroutineTest {
        // Arrange
        val request = mockk<EmployeeModel.CreateEmployeeRequest>()
        coEvery { employeeService.createEmployee(request) } returns Result.success(mockk())
        // Act
        val result = manager.addEmployee(request)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { employeeService.createEmployee(request) }
    }
}

