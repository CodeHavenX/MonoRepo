package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.core.service.models.requests.CreateEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateEventLogEntryRequest
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
import kotlin.test.assertTrue

class SupabaseEventLogDatabaseIntegrationTest : TestBase(), KoinTest {

    private val database: SupabaseEventLogDatabase by inject()
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
    fun `createEventLogEntry should return entry on success`() = runBlockingTest {
        val request = CreateEventLogEntryRequest(
            title = "${'$'}{test_prefix}_EventTitle",
            description = "${'$'}{test_prefix}_EventDescription",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = PropertyId("property_id"), // Use a valid PropertyId
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = kotlinx.datetime.Clock.System.now(),
            unit = "TestUnit", // Use a valid unit
        )

        val result = database.createEventLogEntry(request)

        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getEventLogEntry should return entry if exists`() = runBlockingTest {
        val createRequest = CreateEventLogEntryRequest(
            title = "${'$'}{test_prefix}_EventTitle2",
            description = "${'$'}{test_prefix}_EventDescription2",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = PropertyId("property_id2"), // Use a valid PropertyId
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = kotlinx.datetime.Clock.System.now(),
            unit = "TestUnit2", // Use a valid unit
        )

        val createResult = database.createEventLogEntry(createRequest)
        assertTrue(createResult.isSuccess)

        val createdEntry = createResult.getOrNull()
        assertNotNull(createdEntry)

        val getRequest = GetEventLogEntryRequest(
            id = EventLogEntryId(eventLogEntryId = createdEntry.id.eventLogEntryId)
        )
        val getResult = database.getEventLogEntry(getRequest)

        assertTrue(getResult.isSuccess)
        assertNotNull(getResult.getOrNull())
    }

    @Test
    fun `getEventLogEntries should return all entries`() = runBlockingTest {
        // Create two entries
        val request1 = CreateEventLogEntryRequest(
            title = "${test_prefix}_EventTitleA",
            description = "${test_prefix}_EventDescriptionA",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = PropertyId("property_idA"), // Use a valid PropertyId
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = kotlinx.datetime.Clock.System.now(),
            unit = "TestUnitA", // Use a valid unit
        )
        val request2 = CreateEventLogEntryRequest(
            title = "${test_prefix}_EventTitleB",
            description = "${test_prefix}_EventDescriptionB",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = PropertyId("property_idB"), // Use a valid PropertyId
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = kotlinx.datetime.Clock.System.now(),
            unit = "TestUnitB", // Use a valid unit
        )
        val result1 = database.createEventLogEntry(request1)
        val result2 = database.createEventLogEntry(request2)
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        val getAllResult = database.getEventLogEntries()
        assertTrue(getAllResult.isSuccess)
        val entries = getAllResult.getOrNull()
        assertNotNull(entries)
        // At least the two we just created should be present
        val titles = entries!!.map { it.title }
        assertTrue(titles.contains(request1.title))
        assertTrue(titles.contains(request2.title))
    }

    @Test
    fun `updateEventLogEntry should update entry fields`() = runBlockingTest {
        val createRequest = CreateEventLogEntryRequest(
            title = "${test_prefix}_EventTitleToUpdate",
            description = "${test_prefix}_EventDescriptionToUpdate",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = PropertyId("property_idToUpdate"), // Use a valid PropertyId
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = kotlinx.datetime.Clock.System.now(),
            unit = "TestUnitToUpdate", // Use a valid unit
        )
        val createResult = database.createEventLogEntry(createRequest)
        assertTrue(createResult.isSuccess)
        val createdEntry = createResult.getOrNull()
        assertNotNull(createdEntry)

        val updateRequest = UpdateEventLogEntryRequest(
            id = createdEntry!!.id,
            title = "${test_prefix}_UpdatedTitle",
            description = "${test_prefix}_UpdatedDescription",
            type = EventLogEventType.MAINTENANCE_SERVICE, // Use a valid EventLogEventType
            fallbackEventType = "UpdatedFallbackType", // Set as needed
            unit = "UpdatedUnit", // Use a valid unit
        )
        val updateResult = database.updateEventLogEntry(updateRequest)
        assertTrue(updateResult.isSuccess)
        val updatedEntry = updateResult.getOrNull()
        assertNotNull(updatedEntry)
        assertTrue(updatedEntry!!.title == updateRequest.title)
        assertTrue(updatedEntry.description == updateRequest.description)
    }

    @Test
    fun `deleteEventLogEntry should remove entry`() = runBlockingTest {
        val createRequest = CreateEventLogEntryRequest(
            title = "${test_prefix}_EventTitleToDelete",
            description = "${test_prefix}_EventDescriptionToDelete",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            propertyId = PropertyId("property_idToDelete"), // Use a valid PropertyId
            fallbackEventType = null, // Set as needed
            timestamp = kotlinx.datetime.Clock.System.now(),
            unit = "TestUnitToDelete", // Use a valid unit
        )
        val createResult = database.createEventLogEntry(createRequest)
        assertTrue(createResult.isSuccess)
        val createdEntry = createResult.getOrNull()
        assertNotNull(createdEntry)

        val deleteRequest = DeleteEventLogEntryRequest(
            id = EventLogEntryId(eventLogEntryId = createdEntry!!.id.eventLogEntryId)
        )
        val deleteResult = database.deleteEventLogEntry(deleteRequest)
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)

        // Try to get the deleted entry
        val getRequest = GetEventLogEntryRequest(
            id = EventLogEntryId(eventLogEntryId = createdEntry.id.eventLogEntryId)
        )
        val getResult = database.getEventLogEntry(getRequest)
        assertTrue(getResult.isSuccess)
        assertTrue(getResult.getOrNull() == null)
    }
}
