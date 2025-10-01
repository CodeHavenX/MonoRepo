package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabaseStaffDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var test_prefix: String
    private var propertyId: PropertyId? = null
    private var testUserId: UserId? = null
    private var orgId: OrganizationId? = null

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
        runBlocking {
            testUserId = createTestUser("user-${test_prefix}@test.com")
            orgId = createTestOrganization()
            propertyId = createTestProperty("${test_prefix}_Property", testUserId!!, orgId!!)
        }
    }

    @Test
    fun `createStaff should return staff on success`() = runCoroutineTest {
        // Arrange

        // Act
        val result = staffDatastore.createStaff(
            idType = IdType.PASSPORT,
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
            role = StaffRole.CLEANING,
            propertyId = propertyId!!,
        ).registerStaffForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getStaff should return created staff`() = runCoroutineTest {
        // Arrange

        // Act
        val createResult = staffDatastore.createStaff(
            firstName = "${test_prefix}_GetFirst",
            lastName = "${test_prefix}_GetLast",
            role = StaffRole.SECURITY_COVER,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        ).registerStaffForDeletion()
        assertTrue(createResult.isSuccess)
        val staff = createResult.getOrNull()!!
        val getResult = staffDatastore.getStaff(staff.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertTrue(fetched.firstName == "${test_prefix}_GetFirst")
    }

    @Test
    fun `getStaffs should return all staff for a given user`() = runCoroutineTest {
        // Arrange

        // Act
        val result1 = staffDatastore.createStaff(
            firstName = "${test_prefix}_StaffA",
            lastName = "${test_prefix}_LastA",
            role = StaffRole.SECURITY_COVER,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        ).registerStaffForDeletion()
        val result2 = staffDatastore.createStaff(
            firstName = "${test_prefix}_StaffB",
            lastName = "${test_prefix}_LastB",
            role = StaffRole.CLEANING,
            idType = IdType.PASSPORT,
            propertyId = propertyId!!,
        ).registerStaffForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        val getAllResult = staffDatastore.getStaffs(currentUser = testUserId!!)

        // Assert
        assertTrue(getAllResult.isSuccess)
        val staffs = getAllResult.getOrNull()
        assertNotNull(staffs)
        val firstNames = staffs!!.map { it.firstName }
        assertTrue(firstNames.contains("${test_prefix}_StaffA"))
        assertTrue(firstNames.contains("${test_prefix}_StaffB"))
    }

    @Test
    fun `updateStaff should update staff fields`() = runCoroutineTest {
        // Arrange

        // Act
        val createResult = staffDatastore.createStaff(
            firstName = "${test_prefix}_ToUpdate",
            lastName = "${test_prefix}_LastToUpdate",
            role = StaffRole.SECURITY,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        ).registerStaffForDeletion()
        assertTrue(createResult.isSuccess)
        val staff = createResult.getOrNull()!!
        val updateResult = staffDatastore.updateStaff(
            staffId = staff.id,
            firstName = "${test_prefix}_UpdatedFirst",
            lastName = "${test_prefix}_UpdatedLast",
            role = StaffRole.CLEANING,
            idType = IdType.PASSPORT
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertTrue(updated.firstName == "${test_prefix}_UpdatedFirst")
        assertTrue(updated.lastName == "${test_prefix}_UpdatedLast")
        assertTrue(updated.role == StaffRole.CLEANING)
        assertTrue(updated.idType == IdType.PASSPORT)
    }

    @Test
    fun `deleteStaff should remove staff`() = runCoroutineTest {
        // Arrange
        val createResult = staffDatastore.createStaff(
            firstName = "${test_prefix}_ToDelete",
            lastName = "${test_prefix}_LastToDelete",
            role = StaffRole.SECURITY,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        )
        assertTrue(createResult.isSuccess)
        val staff = createResult.getOrNull()!!

        // Act
        val deleteResult = staffDatastore.deleteStaff(staff.id)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = staffDatastore.getStaff(staff.id)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `deleteStaff should fail for non-existent staff`() = runCoroutineTest {
        // Arrange
        val fakeId = StaffId("fake-$test_prefix")

        // Act
        val deleteResult = staffDatastore.deleteStaff(fakeId)

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }
}
