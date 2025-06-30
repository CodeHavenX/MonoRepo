package com.cramsan.edifikana.client.lib.managers

import com.cramsan.edifikana.client.lib.models.StaffModel
import com.cramsan.edifikana.client.lib.service.StaffService
import com.cramsan.edifikana.lib.model.StaffId
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
 * Unit tests for the StaffManager class.
 */
class StaffManagerTest : CoroutineTest() {
    private lateinit var staffService: StaffService
    private lateinit var dependencies: ManagerDependencies
    private lateinit var manager: StaffManager

    /**
     * Sets up the test environment before each test.
     */
    @BeforeTest
    fun setup() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        staffService = mockk()
        dependencies = mockk(relaxed = true)
        every { dependencies.appScope } returns testCoroutineScope
        every { dependencies.dispatcherProvider } returns UnifiedDispatcherProvider(testCoroutineDispatcher)

        manager = StaffManager(staffService, dependencies)
    }

    /**
     * Tests that getStaffList returns the expected list from the service.
     */
    @Test
    fun `getStaffList returns staff list`() = runCoroutineTest {
        // Arrange
        val staffList = listOf(mockk<StaffModel>(), mockk<StaffModel>())
        coEvery { staffService.getStaffList() } returns Result.success(staffList)
        // Act
        val result = manager.getStaffList()
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(staffList, result.getOrNull())
        coVerify { staffService.getStaffList() }
    }

    /**
     * Tests that getStaff returns the expected staff from the service.
     */
    @Test
    fun `getStaff returns staff`() = runCoroutineTest {
        // Arrange
        val staffId = StaffId("staff-1")
        val staff = mockk<StaffModel>()
        coEvery { staffService.getStaff(staffId) } returns Result.success(staff)
        // Act
        val result = manager.getStaff(staffId)
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(staff, result.getOrNull())
        coVerify { staffService.getStaff(staffId) }
    }

    /**
     * Tests that addStaff calls the service and returns success.
     */
    @Test
    fun `addStaff calls service`() = runCoroutineTest {
        // Arrange
        val request = mockk<StaffModel.CreateStaffRequest>()
        coEvery { staffService.createStaff(request) } returns Result.success(mockk())
        // Act
        val result = manager.addStaff(request)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { staffService.createStaff(request) }
    }

    /**
     * Tests that inviteStaff calls the service and returns success.
     */
    @Test
    fun `inviteStaff calls service`() = runCoroutineTest {
        // Arrange
        val email = "test@example.com"
        coEvery { staffService.inviteStaff(email) } returns Result.success(Unit)
        // Act
        val result = manager.inviteStaff(email)
        // Assert
        assertTrue(result.isSuccess)
        coVerify { staffService.inviteStaff(email) }
    }
}

