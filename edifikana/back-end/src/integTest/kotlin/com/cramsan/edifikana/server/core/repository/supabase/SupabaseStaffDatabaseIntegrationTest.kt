package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.edifikana.server.core.service.models.requests.CreateStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetStaffRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateStaffRequest
import com.cramsan.edifikana.server.di.FrameworkModule
import com.cramsan.edifikana.server.di.IntegTestApplicationModule
import com.cramsan.edifikana.server.di.SettingsModule
import com.cramsan.edifikana.server.di.SupabaseModule
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.utils.uuid.UUID
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabaseStaffDatabaseIntegrationTest : TestBase(), KoinTest {

    private val database: SupabaseStaffDatabase by inject()
    private lateinit var test_prefix: String

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
        startKoin {
            modules(
                FrameworkModule,
                SettingsModule,
                IntegTestApplicationModule,
                SupabaseModule,
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `createStaff should return staff on success`() = runBlockingTest {
        val request = CreateStaffRequest(
            idType = IdType.PASSPORT,
            firstName = "${'$'}{test_prefix}_First",
            lastName = "${'$'}{test_prefix}_Last",
            role = StaffRole.CLEANING,
            propertyId = PropertyId("property-${'$'}{test_prefix}")
        )
        val result = database.createStaff(request)
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getStaff should return created staff`() = runBlockingTest {
        val createRequest = CreateStaffRequest(
            firstName = "${'$'}{test_prefix}_GetFirst",
            lastName = "${'$'}{test_prefix}_GetLast",
            role = StaffRole.SECURITY_COVER,
            idType = IdType.DNI,
            propertyId = PropertyId("property-${'$'}{test_prefix}")
        )
        val createResult = database.createStaff(createRequest)
        assertTrue(createResult.isSuccess)
        val staff = createResult.getOrNull()!!
        val getResult = database.getStaff(GetStaffRequest(staff.id))
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertTrue(fetched.firstName == createRequest.firstName)
    }

    @Test
    fun `getStaffs should return all staff`() = runBlockingTest {
        val request1 = CreateStaffRequest(
            firstName = "${'$'}{test_prefix}_StaffA",
            lastName = "${'$'}{test_prefix}_LastA",
            role = StaffRole.SECURITY_COVER,
            idType = IdType.DNI,
            propertyId = PropertyId("property-${'$'}{test_prefix}"),
        )
        val request2 = CreateStaffRequest(
            firstName = "${'$'}{test_prefix}_StaffB",
            lastName = "${'$'}{test_prefix}_LastB",
            role = StaffRole.CLEANING,
            idType = IdType.PASSPORT,
            propertyId = PropertyId("property-${'$'}{test_prefix}"),
        )
        val result1 = database.createStaff(request1)
        val result2 = database.createStaff(request2)
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        val getAllResult = database.getStaffs()
        assertTrue(getAllResult.isSuccess)
        val staffs = getAllResult.getOrNull()
        assertNotNull(staffs)
        val firstNames = staffs!!.map { it.firstName }
        assertTrue(firstNames.contains(request1.firstName))
        assertTrue(firstNames.contains(request2.firstName))
    }

    @Test
    fun `updateStaff should update staff fields`() = runBlockingTest {
        val createRequest = CreateStaffRequest(
            firstName = "${'$'}{test_prefix}_ToUpdate",
            lastName = "${'$'}{test_prefix}_LastToUpdate",
            role = StaffRole.SECURITY,
            idType = IdType.DNI,
            propertyId = PropertyId("property-${'$'}{test_prefix}")
        )
        val createResult = database.createStaff(createRequest)
        assertTrue(createResult.isSuccess)
        val staff = createResult.getOrNull()!!
        val updateRequest = UpdateStaffRequest(
            id = staff.id,
            firstName = "${'$'}{test_prefix}_UpdatedFirst",
            lastName = "${'$'}{test_prefix}_UpdatedLast",
            role = StaffRole.CLEANING,
            idType = IdType.PASSPORT
        )
        val updateResult = database.updateStaff(updateRequest)
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
        val createRequest = CreateStaffRequest(
            firstName = "${'$'}{test_prefix}_ToDelete",
            lastName = "${'$'}{test_prefix}_LastToDelete",
            role = StaffRole.SECURITY,
            idType = IdType.DNI,
            propertyId = PropertyId("property-${'$'}{test_prefix}")
        )
        val createResult = database.createStaff(createRequest)
        assertTrue(createResult.isSuccess)
        val staff = createResult.getOrNull()!!
        val deleteResult = database.deleteStaff(DeleteStaffRequest(staff.id))
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = database.getStaff(GetStaffRequest(staff.id))
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `deleteStaff should fail for non-existent staff`() = runBlockingTest {
        val fakeId = StaffId("fake-${'$'}test_prefix")
        val deleteResult = database.deleteStaff(DeleteStaffRequest(fakeId))
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }
}

