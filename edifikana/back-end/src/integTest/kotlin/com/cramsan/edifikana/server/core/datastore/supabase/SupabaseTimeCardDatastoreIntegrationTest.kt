package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SupabaseTimeCardDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var test_prefix: String
    private var propertyId: PropertyId? = null
    private var employeeId: EmployeeId? = null
    private var testUserId: UserId? = null
    private var orgId: OrganizationId? = null

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
        runBlocking {
            testUserId = createTestUser("user-${test_prefix}@test.com")
            orgId = createTestOrganization()
            propertyId = createTestProperty("${test_prefix}_Property", testUserId!!, orgId!!)
            employeeId = createTestEmployee(
                propertyId = propertyId!!,
                firstName = "${test_prefix}_FirstName",
                lastName = "${test_prefix}_LastName",
            )
        }
    }

    @Test
    fun `createTimeCardEvent should return event on success`() = runCoroutineTest {
        // Arrange

        // Act
        val result = timeCardDatastore.createTimeCardEvent(
            fallbackEmployeeName = "${test_prefix}_FallbackEmployeeName",
            employeeId = employeeId!!,
            propertyId = propertyId!!,
            type = TimeCardEventType.CLOCK_OUT,
            imageUrl = null,
            timestamp = Clock.System.now(),
        ).registerTimeCardEventForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getTimeCardEvent should return created event`() = runCoroutineTest {
        // Arrange

        // Act
        val createResult = timeCardDatastore.createTimeCardEvent(
            fallbackEmployeeName = "${test_prefix}_FallbackEmployeeName",
            employeeId = employeeId!!,
            propertyId = propertyId!!,
            type = TimeCardEventType.CLOCK_IN,
            imageUrl = null,
            timestamp = Clock.System.now(),
        ).registerTimeCardEventForDeletion()
        assertTrue(createResult.isSuccess)
        val event = createResult.getOrNull()!!
        val getResult = timeCardDatastore.getTimeCardEvent(event.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
    }

    @Test
    fun `getTimeCardEventList should return all events`() = runCoroutineTest {
        // Arrange

        // Act
        val result1 = timeCardDatastore.createTimeCardEvent(
            fallbackEmployeeName = "${test_prefix}_FallbackEmployeeName1",
            employeeId = employeeId!!,
            propertyId = propertyId!!,
            type = TimeCardEventType.CLOCK_IN,
            timestamp = Clock.System.now(),
            imageUrl = null,
        ).registerTimeCardEventForDeletion()
        val result2 = timeCardDatastore.createTimeCardEvent(
            employeeId = employeeId!!,
            fallbackEmployeeName = "${test_prefix}_FallbackEmployeeName2",
            propertyId = propertyId!!,
            type = TimeCardEventType.CLOCK_OUT,
            timestamp = Clock.System.now(),
            imageUrl = null,
        ).registerTimeCardEventForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        val getAllResult = timeCardDatastore.getTimeCardEvents(employeeId!!)

        // Assert
        assertTrue(getAllResult.isSuccess)

        val events = getAllResult.getOrNull()
        assertNotNull(events)
    }

    @Test
    fun `getTimeCardEvent should return null for non-existent event`() = runCoroutineTest {
        // Arrange
        val fakeId = TimeCardEventId(UUID.random())

        // Act
        val getResult = timeCardDatastore.getTimeCardEvent(fakeId)

        // Assert
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }
}
