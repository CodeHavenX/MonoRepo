package com.cramsan.edifikana.server.core.service

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.datastore.StaffDatastore
import com.cramsan.edifikana.server.core.service.models.Staff
import com.cramsan.edifikana.server.core.service.models.UserRole
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
 * Test class for [StaffService].
 */
class StaffServiceTest {
    private lateinit var staffDatastore: StaffDatastore
    private lateinit var staffService: StaffService

    /**
     * Sets up the test environment by initializing mocks for [StaffDatastore] and [staffService].
     */
    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        staffDatastore = mockk()
        staffService = StaffService(staffDatastore)
    }

    /**
     * Cleans up the test environment by stopping Koin.
     */
    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Tests that createStaff creates a staff and returns it.
     */
    @Test
    fun `createStaff should call staffDatastore and return staff`() = runTest {
        // Arrange
        val staff = mockk<Staff>()
        val idType = IdType.DNI
        val firstName = "John"
        val lastName = "Doe"
        val role = StaffRole.MANAGER
        val propertyId = PropertyId("property-1")
        coEvery {
            staffDatastore.createStaff(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.success(staff)

        // Act
        val result = staffService.createStaff(idType, firstName, lastName, role, propertyId)

        // Assert
        assertEquals(staff, result)
        coVerify {
            staffDatastore.createStaff(
                idType,
                firstName,
                lastName,
                role,
                propertyId,
            )
        }
    }

    /**
     * Tests that getStaff retrieves a staff by ID and returns it.
     */
    @Test
    fun `getStaff should call staffDatastore and return staff`() = runTest {
        // Arrange
        val staff = mockk<Staff>()
        val staffId = StaffId("staff-1")
        coEvery { staffDatastore.getStaff(any()) } returns Result.success(staff)

        // Act
        val result = staffService.getStaff(staffId)

        // Assert
        assertEquals(staff, result)
        coVerify { staffDatastore.getStaff(staffId) }
    }

    /**
     * Tests that getStaff returns null if the staff is not found.
     */
    @Test
    fun `getStaff should return null if not found`() = runTest {
        // Arrange
        val staffId = StaffId("staff-2")
        coEvery { staffDatastore.getStaff(any()) } returns Result.failure(Exception("Not found"))

        // Act
        val result = staffService.getStaff(staffId)

        // Assert
        assertNull(result)
        coVerify { staffDatastore.getStaff(staffId) }
    }

    /**
     * Tests that getStaffs retrieves all staffs and returns a list.
     */
    @Test
    fun `getStaffs should call staffDatastore and return list`() = runTest {
        // Arrange
        val staffList = listOf(mockk<Staff>(), mockk<Staff>())
        val request = UserId("user-1")
        val clientContext = ClientContext.AuthenticatedClientContext(
            userInfo = mockk(),
            userId = UserId("user-1"),
            userRole = UserRole.SUPERUSER,
        )
        coEvery { staffDatastore.getStaffs(request) } returns Result.success(staffList)

        // Act
        val result = staffService.getStaffs(clientContext)

        // Assert
        assertEquals(staffList, result)
        coVerify { staffDatastore.getStaffs(request) }
    }

    /**
     * Tests that updateStaff updates a staff and returns the updated staff.
     */
    @Test
    fun `updateStaff should call staffDatastore and return updated staff`() = runTest {
        // Arrange
        val staff = mockk<Staff>()
        val staffId = StaffId("staff-3")
        val idType = IdType.DNI
        val firstName = "Jane"
        val lastName = "Smith"
        val role = StaffRole.MANAGER
        coEvery {
            staffDatastore.updateStaff(
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } returns Result.success(staff)

        // Act
        val result = staffService.updateStaff(staffId, idType, firstName, lastName, role)

        // Assert
        assertEquals(staff, result)
        coVerify {
            staffDatastore.updateStaff(
                staffId,
                idType,
                firstName,
                lastName,
                role,
            )
        }
    }

    /**
     * Tests that deleteStaff deletes a staff and returns true.
     */
    @Test
    fun `deleteStaff should call staffDatastore and return true`() = runTest {
        // Arrange
        val staffId = StaffId("staff-4")
        coEvery { staffDatastore.deleteStaff(any()) } returns Result.success(true)

        // Act
        val result = staffService.deleteStaff(staffId)

        // Assert
        assertEquals(true, result)
        coVerify { staffDatastore.deleteStaff(staffId) }
    }
}
