package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.server.core.service.models.requests.CreateStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateStaffRequest
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabaseStaffDatabaseIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var test_prefix: String
    private var propertyId: PropertyId? = null

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
        runBlocking {
            propertyId = createTestProperty("${test_prefix}_Property")
        }
    }

    @Test
    fun `createStaff should return staff on success`() = runBlockingTest {
        // Arrange
        val request = CreateStaffRequest(
            idType = IdType.PASSPORT,
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
            role = StaffRole.CLEANING,
            propertyId = propertyId!!,
        )

        // Act
        val result = staffDatabase.createStaff(request).registerStaffForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getStaff should return created staff`() = runBlockingTest {
        // Arrange
        val createRequest = CreateStaffRequest(
            firstName = "${test_prefix}_GetFirst",
            lastName = "${test_prefix}_GetLast",
            role = StaffRole.SECURITY_COVER,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        )

        // Act
        val createResult = staffDatabase.createStaff(createRequest).registerStaffForDeletion()
        assertTrue(createResult.isSuccess)
        val staff = createResult.getOrNull()!!
        val getResult = staffDatabase.getStaff(GetStaffRequest(staff.id))

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertTrue(fetched.firstName == createRequest.firstName)
    }

    @Test
    fun `getStaffs should return all staff`() = runBlockingTest {
        // Arrange
        val request1 = CreateStaffRequest(
            firstName = "${test_prefix}_StaffA",
            lastName = "${test_prefix}_LastA",
            role = StaffRole.SECURITY_COVER,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        )
        val request2 = CreateStaffRequest(
            firstName = "${test_prefix}_StaffB",
            lastName = "${test_prefix}_LastB",
            role = StaffRole.CLEANING,
            idType = IdType.PASSPORT,
            propertyId = propertyId!!,
        )

        // Act
        val result1 = staffDatabase.createStaff(request1).registerStaffForDeletion()
        val result2 = staffDatabase.createStaff(request2).registerStaffForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        val getAllResult = staffDatabase.getStaffs()

        // Assert
        assertTrue(getAllResult.isSuccess)
        val staffs = getAllResult.getOrNull()
        assertNotNull(staffs)
        val firstNames = staffs!!.map { it.firstName }
        assertTrue(firstNames.contains(request1.firstName))
        assertTrue(firstNames.contains(request2.firstName))
    }

    @Test
    fun `updateStaff should update staff fields`() = runBlockingTest {
        // Arrange
        val createRequest = CreateStaffRequest(
            firstName = "${test_prefix}_ToUpdate",
            lastName = "${test_prefix}_LastToUpdate",
            role = StaffRole.SECURITY,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        )

        // Act
        val createResult = staffDatabase.createStaff(createRequest).registerStaffForDeletion()
        assertTrue(createResult.isSuccess)
        val staff = createResult.getOrNull()!!
        val updateRequest = UpdateStaffRequest(
            id = staff.id,
            firstName = "${test_prefix}_UpdatedFirst",
            lastName = "${test_prefix}_UpdatedLast",
            role = StaffRole.CLEANING,
            idType = IdType.PASSPORT
        )
        val updateResult = staffDatabase.updateStaff(updateRequest)

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertTrue(updated.firstName == updateRequest.firstName)
        assertTrue(updated.lastName == updateRequest.lastName)
        assertTrue(updated.role == updateRequest.role)
        assertTrue(updated.idType == updateRequest.idType)
    }

    @Test
    fun `deleteStaff should remove staff`() = runBlockingTest {
        // Arrange
        val createRequest = CreateStaffRequest(
            firstName = "${test_prefix}_ToDelete",
            lastName = "${test_prefix}_LastToDelete",
            role = StaffRole.SECURITY,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        )
        val createResult = staffDatabase.createStaff(createRequest)
        assertTrue(createResult.isSuccess)
        val staff = createResult.getOrNull()!!

        // Act
        val deleteResult = staffDatabase.deleteStaff(DeleteStaffRequest(staff.id))

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = staffDatabase.getStaff(GetStaffRequest(staff.id))
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `deleteStaff should fail for non-existent staff`() = runBlockingTest {
        // Arrange
        val fakeId = StaffId("fake-$test_prefix")

        // Act
        val deleteResult = staffDatabase.deleteStaff(DeleteStaffRequest(fakeId))

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }
}
