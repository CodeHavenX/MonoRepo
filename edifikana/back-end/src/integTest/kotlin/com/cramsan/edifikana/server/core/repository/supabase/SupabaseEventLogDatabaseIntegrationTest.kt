package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.server.core.service.models.requests.CreateEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeleteEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetEventLogEntryRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdateEventLogEntryRequest
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SupabaseEventLogDatabaseIntegrationTest : SupabaseIntegrationTest() {

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
    fun `createEventLogEntry should return entry on success`() = runCoroutineTest {
        // Arrange
        val request = CreateEventLogEntryRequest(
            title = "${test_prefix}_EventTitle",
            description = "${test_prefix}_EventDescription",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = propertyId!!,
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = Clock.System.now(),
            unit = "TestUnit", // Use a valid unit
        )
        // Act
        val result = eventLogDatabase.createEventLogEntry(request).registerEventLogEntryForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getEventLogEntry should return entry if exists`() = runCoroutineTest {
        // Arrange
        val createRequest = CreateEventLogEntryRequest(
            title = "${test_prefix}_EventTitle2",
            description = "${test_prefix}_EventDescription2",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = propertyId!!,
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = Clock.System.now(),
            unit = "TestUnit2", // Use a valid unit
        )

        // Act
        val createResult = eventLogDatabase.createEventLogEntry(createRequest).registerEventLogEntryForDeletion()
        assertTrue(createResult.isSuccess)
        val createdEntry = createResult.getOrNull()
        assertNotNull(createdEntry)
        val getRequest = GetEventLogEntryRequest(
            id = EventLogEntryId(eventLogEntryId = createdEntry.id.eventLogEntryId)
        )
        val getResult = eventLogDatabase.getEventLogEntry(getRequest)

        // Assert
        assertTrue(getResult.isSuccess)
        assertNotNull(getResult.getOrNull())
    }

    @Test
    fun `getEventLogEntries should return all entries`() = runCoroutineTest {
        // Arrange
        val request1 = CreateEventLogEntryRequest(
            title = "${test_prefix}_EventTitleA",
            description = "${test_prefix}_EventDescriptionA",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = propertyId!!,
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = Clock.System.now(),
            unit = "TestUnitA", // Use a valid unit
        )
        val request2 = CreateEventLogEntryRequest(
            title = "${test_prefix}_EventTitleB",
            description = "${test_prefix}_EventDescriptionB",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = propertyId!!,
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = Clock.System.now(),
            unit = "TestUnitB", // Use a valid unit
        )

        // Act
        val result1 = eventLogDatabase.createEventLogEntry(request1).registerEventLogEntryForDeletion()
        val result2 = eventLogDatabase.createEventLogEntry(request2).registerEventLogEntryForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        val getAllResult = eventLogDatabase.getEventLogEntries()

        // Assert
        assertTrue(getAllResult.isSuccess)
        val entries = getAllResult.getOrNull()
        assertNotNull(entries)
        // At least the two we just created should be present
        val titles = entries!!.map { it.title }
        assertTrue(titles.contains(request1.title))
        assertTrue(titles.contains(request2.title))
    }

    @Test
    fun `updateEventLogEntry should update entry fields`() = runCoroutineTest {
        // Arrange
        val createRequest = CreateEventLogEntryRequest(
            title = "${test_prefix}_EventTitleToUpdate",
            description = "${test_prefix}_EventDescriptionToUpdate",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = propertyId!!,
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = Clock.System.now(),
            unit = "TestUnitToUpdate", // Use a valid unit
        )

        // Act
        val createResult = eventLogDatabase.createEventLogEntry(createRequest).registerEventLogEntryForDeletion()
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
        val updateResult = eventLogDatabase.updateEventLogEntry(updateRequest)

        // Assert
        assertTrue(updateResult.isSuccess)
        val updatedEntry = updateResult.getOrNull()
        assertNotNull(updatedEntry)
        assertTrue(updatedEntry!!.title == updateRequest.title)
        assertTrue(updatedEntry.description == updateRequest.description)
    }

    @Test
    fun `deleteEventLogEntry should remove entry`() = runCoroutineTest {
        // Arrange
        val createRequest = CreateEventLogEntryRequest(
            title = "${test_prefix}_EventTitleToDelete",
            description = "${test_prefix}_EventDescriptionToDelete",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            propertyId = propertyId!!,
            fallbackEventType = null, // Set as needed
            timestamp = Clock.System.now(),
            unit = "TestUnitToDelete", // Use a valid unit
        )
        val createResult = eventLogDatabase.createEventLogEntry(createRequest)
        assertTrue(createResult.isSuccess)
        val createdEntry = createResult.getOrNull()
        assertNotNull(createdEntry)
        val deleteRequest = DeleteEventLogEntryRequest(
            id = EventLogEntryId(eventLogEntryId = createdEntry!!.id.eventLogEntryId)
        )

        // Act
        val deleteResult = eventLogDatabase.deleteEventLogEntry(deleteRequest)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        // Try to get the deleted entry
        val getRequest = GetEventLogEntryRequest(
            id = EventLogEntryId(eventLogEntryId = createdEntry.id.eventLogEntryId)
        )
        val getResult = eventLogDatabase.getEventLogEntry(getRequest)
        assertTrue(getResult.isSuccess)
        assertTrue(getResult.getOrNull() == null)
    }
}
