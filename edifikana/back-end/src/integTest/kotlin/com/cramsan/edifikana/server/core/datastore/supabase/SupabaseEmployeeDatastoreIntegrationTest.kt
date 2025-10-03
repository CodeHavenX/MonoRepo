package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabaseEmployeeDatastoreIntegrationTest : SupabaseIntegrationTest() {

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
    fun `createEmployee should return employee on success`() = runCoroutineTest {
        // Arrange

        // Act
        val result = employeeDatastore.createEmployee(
            idType = IdType.PASSPORT,
            firstName = "${test_prefix}_First",
            lastName = "${test_prefix}_Last",
            role = EmployeeRole.CLEANING,
            propertyId = propertyId!!,
        ).registerEmployeeForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getEmployee should return created employee`() = runCoroutineTest {
        // Arrange

        // Act
        val createResult = employeeDatastore.createEmployee(
            firstName = "${test_prefix}_GetFirst",
            lastName = "${test_prefix}_GetLast",
            role = EmployeeRole.SECURITY_COVER,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        ).registerEmployeeForDeletion()
        assertTrue(createResult.isSuccess)
        val employee = createResult.getOrNull()!!
        val getResult = employeeDatastore.getEmployee(employee.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertTrue(fetched.firstName == "${test_prefix}_GetFirst")
    }

    @Test
    fun `getEmployees should return all employee for a given user`() = runCoroutineTest {
        // Arrange

        // Act
        val result1 = employeeDatastore.createEmployee(
            firstName = "${test_prefix}_EmployeeA",
            lastName = "${test_prefix}_LastA",
            role = EmployeeRole.SECURITY_COVER,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        ).registerEmployeeForDeletion()
        val result2 = employeeDatastore.createEmployee(
            firstName = "${test_prefix}_EmployeeB",
            lastName = "${test_prefix}_LastB",
            role = EmployeeRole.CLEANING,
            idType = IdType.PASSPORT,
            propertyId = propertyId!!,
        ).registerEmployeeForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        val getAllResult = employeeDatastore.getEmployees(currentUser = testUserId!!)

        // Assert
        assertTrue(getAllResult.isSuccess)
        val employees = getAllResult.getOrNull()
        assertNotNull(employees)
        val firstNames = employees!!.map { it.firstName }
        assertTrue(firstNames.contains("${test_prefix}_EmployeeA"))
        assertTrue(firstNames.contains("${test_prefix}_EmployeeB"))
    }

    @Test
    fun `updateEmployee should update employee fields`() = runCoroutineTest {
        // Arrange

        // Act
        val createResult = employeeDatastore.createEmployee(
            firstName = "${test_prefix}_ToUpdate",
            lastName = "${test_prefix}_LastToUpdate",
            role = EmployeeRole.SECURITY,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        ).registerEmployeeForDeletion()
        assertTrue(createResult.isSuccess)
        val employee = createResult.getOrNull()!!
        val updateResult = employeeDatastore.updateEmployee(
            employeeId = employee.id,
            firstName = "${test_prefix}_UpdatedFirst",
            lastName = "${test_prefix}_UpdatedLast",
            role = EmployeeRole.CLEANING,
            idType = IdType.PASSPORT
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertTrue(updated.firstName == "${test_prefix}_UpdatedFirst")
        assertTrue(updated.lastName == "${test_prefix}_UpdatedLast")
        assertTrue(updated.role == EmployeeRole.CLEANING)
        assertTrue(updated.idType == IdType.PASSPORT)
    }

    @Test
    fun `deleteEmployee should remove employee`() = runCoroutineTest {
        // Arrange
        val createResult = employeeDatastore.createEmployee(
            firstName = "${test_prefix}_ToDelete",
            lastName = "${test_prefix}_LastToDelete",
            role = EmployeeRole.SECURITY,
            idType = IdType.DNI,
            propertyId = propertyId!!,
        )
        assertTrue(createResult.isSuccess)
        val employee = createResult.getOrNull()!!

        // Act
        val deleteResult = employeeDatastore.deleteEmployee(employee.id)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = employeeDatastore.getEmployee(employee.id)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `deleteEmployee should fail for non-existent employee`() = runCoroutineTest {
        // Arrange
        val fakeId = EmployeeId("fake-$test_prefix")

        // Act
        val deleteResult = employeeDatastore.deleteEmployee(fakeId)

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }
}
