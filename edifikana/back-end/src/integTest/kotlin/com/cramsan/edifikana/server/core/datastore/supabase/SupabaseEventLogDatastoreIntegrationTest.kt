package com.cramsan.edifikana.server.core.datastore.supabase

import com.cramsan.edifikana.lib.model.EventLogEntryId
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SupabaseEventLogDatastoreIntegrationTest : SupabaseIntegrationTest() {

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
    fun `createEventLogEntry should return entry on success`() = runCoroutineTest {
        // Arrange
        val title = "${test_prefix}_EventTitle"
        val description = "${test_prefix}_EventDescription"
        val staffId = null // Set as needed
        val fallbackStaffName = null // Set as needed
        val propertyId = propertyId!!
        val type = EventLogEventType.DELIVERY // Use a valid EventLogEventType
        val fallbackEventType = null // Set as needed
        val timestamp = Clock.System.now()
        val unit = "TestUnit" // Use a valid unit
        // Act
        val result = eventLogDatastore.createEventLogEntry(
            title = title,
            description = description,
            staffId = staffId,
            fallbackStaffName = fallbackStaffName,
            propertyId = propertyId,
            type = type,
            fallbackEventType = fallbackEventType,
            timestamp = timestamp,
            unit = unit,
        ).registerEventLogEntryForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun `getEventLogEntry should return entry if exists`() = runCoroutineTest {
        // Arrange
        val title = "${test_prefix}_EventTitle2"
        val description = "${test_prefix}_EventDescription2"
        val staffId = null // Set as needed
        val fallbackStaffName = null // Set as needed
        val propertyId = propertyId!!
        val type = EventLogEventType.DELIVERY // Use a valid EventLogEventType
        val fallbackEventType = null // Set as needed
        val timestamp = Clock.System.now()
        val unit = "TestUnit2" // Use a valid unit

        // Act
        val createResult = eventLogDatastore.createEventLogEntry(
            title = title,
            description = description,
            staffId = staffId,
            fallbackStaffName = fallbackStaffName,
            propertyId = propertyId,
            type = type,
            fallbackEventType = fallbackEventType,
            timestamp = timestamp,
            unit = unit,
        ).registerEventLogEntryForDeletion()
        assertTrue(createResult.isSuccess)
        val createdEntry = createResult.getOrNull()
        assertNotNull(createdEntry)
        val getResult = eventLogDatastore.getEventLogEntry(EventLogEntryId(eventLogEntryId = createdEntry.id.eventLogEntryId))

        // Assert
        assertTrue(getResult.isSuccess)
        assertNotNull(getResult.getOrNull())
    }

    @Test
    fun `getEventLogEntries should return all entries`() = runCoroutineTest {
        // Arrange
        val title1 = "${test_prefix}_EventTitleA"
        val title2 = "${test_prefix}_EventTitleB"

        // Act
        val result1 = eventLogDatastore.createEventLogEntry(
            title = title1,
            description = "${test_prefix}_EventDescriptionA",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = propertyId!!,
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = Clock.System.now(),
            unit = "TestUnitA", // Use a valid unit

        ).registerEventLogEntryForDeletion()
        val result2 = eventLogDatastore.createEventLogEntry(
            title = title2,
            description = "${test_prefix}_EventDescriptionB",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = propertyId!!,
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = Clock.System.now(),
            unit = "TestUnitB", // Use a valid unit
        ).registerEventLogEntryForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        val getAllResult = eventLogDatastore.getEventLogEntries()

        // Assert
        assertTrue(getAllResult.isSuccess)
        val entries = getAllResult.getOrNull()
        assertNotNull(entries)
        // At least the two we just created should be present
        val titles = entries!!.map { it.title }
        assertTrue(titles.contains(title1))
        assertTrue(titles.contains(title2))
    }

    @Test
    fun `updateEventLogEntry should update entry fields`() = runCoroutineTest {
        // Arrange

        // Act
        val createResult = eventLogDatastore.createEventLogEntry(
            title = "${test_prefix}_EventTitleToUpdate",
            description = "${test_prefix}_EventDescriptionToUpdate",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            propertyId = propertyId!!,
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            fallbackEventType = null, // Set as needed
            timestamp = Clock.System.now(),
            unit = "TestUnitToUpdate", // Use a valid unit

        ).registerEventLogEntryForDeletion()
        assertTrue(createResult.isSuccess)
        val createdEntry = createResult.getOrNull()
        assertNotNull(createdEntry)
        val updateResult = eventLogDatastore.updateEventLogEntry(
            id = createdEntry!!.id,
            title = "${test_prefix}_UpdatedTitle",
            description = "${test_prefix}_UpdatedDescription",
            type = EventLogEventType.MAINTENANCE_SERVICE, // Use a valid EventLogEventType
            fallbackEventType = "UpdatedFallbackType", // Set as needed
            unit = "UpdatedUnit", // Use a valid unit
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        val updatedEntry = updateResult.getOrNull()
        assertNotNull(updatedEntry)
        assertTrue(updatedEntry!!.title == "${test_prefix}_UpdatedTitle")
        assertTrue(updatedEntry.description == "${test_prefix}_UpdatedDescription")
    }

    @Test
    fun `deleteEventLogEntry should remove entry`() = runCoroutineTest {
        // Arrange
        val createResult = eventLogDatastore.createEventLogEntry(title = "${test_prefix}_EventTitleToDelete",
            description = "${test_prefix}_EventDescriptionToDelete",
            staffId = null, // Set as needed
            fallbackStaffName = null, // Set as needed
            type = EventLogEventType.DELIVERY, // Use a valid EventLogEventType
            propertyId = propertyId!!,
            fallbackEventType = null, // Set as needed
            timestamp = Clock.System.now(),
            unit = "TestUnitToDelete", // Use a valid unit
        )
        assertTrue(createResult.isSuccess)
        val createdEntry = createResult.getOrNull()
        assertNotNull(createdEntry)

        // Act
        val deleteResult = eventLogDatastore.deleteEventLogEntry(
            id = EventLogEntryId(eventLogEntryId = createdEntry!!.id.eventLogEntryId)
        )

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        // Try to get the deleted entry
        val getResult = eventLogDatastore.getEventLogEntry(
            id = EventLogEntryId(eventLogEntryId = createdEntry.id.eventLogEntryId)
        )
        assertTrue(getResult.isSuccess)
        assertTrue(getResult.getOrNull() == null)
    }
}
