package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.TimeCardEventId
import com.cramsan.edifikana.lib.model.TimeCardEventType
import com.cramsan.edifikana.server.core.service.models.requests.CreateTimeCardEventRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventListRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetTimeCardEventRequest
import com.cramsan.edifikana.server.di.FrameworkModule
import com.cramsan.edifikana.server.di.IntegTestApplicationModule
import com.cramsan.edifikana.server.di.SettingsModule
import com.cramsan.edifikana.server.di.SupabaseModule
import com.cramsan.framework.test.TestBase
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.datetime.Clock
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

class SupabaseTimeCardDatabaseIntegrationTest : TestBase(), KoinTest {

    private val database: SupabaseTimeCardDatabase by inject()
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
    fun `createTimeCardEvent should return event on success`() = runBlockingTest {
        val request = CreateTimeCardEventRequest(
            fallbackStaffName = "${test_prefix}_FallbackStaffName",
            staffId = StaffId("${test_prefix}_test_staff_id"),
            propertyId = PropertyId("${test_prefix}_test_property_id"),
            type = TimeCardEventType.CLOCK_OUT,
            imageUrl = null,
            timestamp = Clock.System.now(),
        )
        val result = database.createTimeCardEvent(request)
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getTimeCardEvent should return created event`() = runBlockingTest {
        val createRequest = CreateTimeCardEventRequest(
            fallbackStaffName = "${test_prefix}_FallbackStaffName",
            staffId = StaffId("${test_prefix}_test_staff_id"),
            propertyId = PropertyId("${test_prefix}_test_property_id"),
            type = TimeCardEventType.CLOCK_IN,
            imageUrl = null,
            timestamp = Clock.System.now(),
        )
        val createResult = database.createTimeCardEvent(createRequest)
        assertTrue(createResult.isSuccess)
        val event = createResult.getOrNull()!!
        val getResult = database.getTimeCardEvent(GetTimeCardEventRequest(event.id))
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
    }

    @Test
    fun `getTimeCardEventList should return all events`() = runBlockingTest {
        val request1 = CreateTimeCardEventRequest(
            fallbackStaffName = "${test_prefix}_FallbackStaffName1",
            staffId = StaffId("${test_prefix}_test_staff_id1"),
            propertyId = PropertyId("${test_prefix}_test_property_id1"),
            type = TimeCardEventType.CLOCK_IN,
            timestamp = Clock.System.now(),
            imageUrl = null,
        )
        val request2 = CreateTimeCardEventRequest(
            staffId = StaffId("${test_prefix}_test_staff_id2"),
            fallbackStaffName = "${test_prefix}_FallbackStaffName2",
            propertyId = PropertyId("${test_prefix}_test_property_id2"),
            type = TimeCardEventType.CLOCK_OUT,
            timestamp = Clock.System.now(),
            imageUrl = null,
        )
        val result1 = database.createTimeCardEvent(request1)
        val result2 = database.createTimeCardEvent(request2)
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        val getAllResult = database.getTimeCardEvents(
            GetTimeCardEventListRequest(StaffId("${test_prefix}_test_staff_id1"))
        )
        assertTrue(getAllResult.isSuccess)

        val events = getAllResult.getOrNull()
        assertNotNull(events)
    }

    @Test
    fun `getTimeCardEvent should return null for non-existent event`() = runBlockingTest {
        val fakeId = TimeCardEventId("fake-${'$'}test_prefix")
        val getResult = database.getTimeCardEvent(GetTimeCardEventRequest(fakeId))
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }
}

