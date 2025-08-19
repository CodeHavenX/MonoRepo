package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.requests.CreateTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventListRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventRequest
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
    private var staffId: StaffId? = null
    private var testUserId: UserId? = null

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
        runBlocking {
            testUserId = createTestUser("user-${test_prefix}@test.com")
            propertyId = createTestProperty("${test_prefix}_Property", testUserId!!)
            staffId = createTestStaff(
                propertyId = propertyId!!,
                firstName = "${test_prefix}_FirstName",
                lastName = "${test_prefix}_LastName",
            )
        }
    }

    @Test
    fun `createTimeCardEvent should return event on success`() = runCoroutineTest {
        // Arrange
        val request = CreateTimeCardEventRequest(
            fallbackStaffName = "${test_prefix}_FallbackStaffName",
            staffId = staffId!!,
            propertyId = propertyId!!,
            type = TimeCardEventType.CLOCK_OUT,
            imageUrl = null,
            timestamp = Clock.System.now(),
        )

        // Act
        val result = timeCardDatastore.createTimeCardEvent(request).registerTimeCardEventForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getTimeCardEvent should return created event`() = runCoroutineTest {
        // Arrange
        val createRequest = CreateTimeCardEventRequest(
            fallbackStaffName = "${test_prefix}_FallbackStaffName",
            staffId = staffId!!,
            propertyId = propertyId!!,
            type = TimeCardEventType.CLOCK_IN,
            imageUrl = null,
            timestamp = Clock.System.now(),
        )

        // Act
        val createResult = timeCardDatastore.createTimeCardEvent(createRequest).registerTimeCardEventForDeletion()
        assertTrue(createResult.isSuccess)
        val event = createResult.getOrNull()!!
        val getResult = timeCardDatastore.getTimeCardEvent(GetTimeCardEventRequest(event.id))

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
    }

    @Test
    fun `getTimeCardEventList should return all events`() = runCoroutineTest {
        // Arrange
        val request1 = CreateTimeCardEventRequest(
            fallbackStaffName = "${test_prefix}_FallbackStaffName1",
            staffId = staffId!!,
            propertyId = propertyId!!,
            type = TimeCardEventType.CLOCK_IN,
            timestamp = Clock.System.now(),
            imageUrl = null,
        )
        val request2 = CreateTimeCardEventRequest(
            staffId = staffId!!,
            fallbackStaffName = "${test_prefix}_FallbackStaffName2",
            propertyId = propertyId!!,
            type = TimeCardEventType.CLOCK_OUT,
            timestamp = Clock.System.now(),
            imageUrl = null,
        )

        // Act
        val result1 = timeCardDatastore.createTimeCardEvent(request1).registerTimeCardEventForDeletion()
        val result2 = timeCardDatastore.createTimeCardEvent(request2).registerTimeCardEventForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        val getAllResult = timeCardDatastore.getTimeCardEvents(
            GetTimeCardEventListRequest(staffId!!)
        )

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
        val getResult = timeCardDatastore.getTimeCardEvent(GetTimeCardEventRequest(fakeId))

        // Assert
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }
}
