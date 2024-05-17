package com.cramsan.edifikana.server.models

import com.cramsan.edifikana.lib.firestore.*
import com.cramsan.edifikana.lib.firestore.helpers.eventTypeFriendlyName
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk
import com.google.cloud.firestore.DocumentSnapshot

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

class MappersKtTest {

    lateinit var documentSnapshot: DocumentSnapshot;

    /**
     * Set up our documentSnapshot
     */
    @BeforeEach
    fun setUp() {
        documentSnapshot = mockk<DocumentSnapshot>()
    }

    /**
     * Test the employee log is created from the documentSnapshot
     */
    @OptIn(FireStoreModel::class)
    @Test
    fun `test documentSnapshot populates Employee Log with details`() {
        // SETUP
        every { documentSnapshot.getString("id") } returns "testId"
        every { documentSnapshot.getString("idType") } returns "DNI"
        every { documentSnapshot.getString("name") } returns "Javier"
        every { documentSnapshot.getString("lastName") } returns "Sanchez Inez"
        every { documentSnapshot.getString("role") } returns "SECURITY"

        // ACT
        val employee = documentSnapshot.toEmployee()

        // ASSERT
        assertEquals("testId", employee.id)
        assertEquals(IdType.DNI, employee.idType)
        assertEquals("Javier", employee.name)
        assertEquals("Sanchez Inez", employee.lastName)
        assertEquals(EmployeeRole.SECURITY, employee.role)
    }

    /**
     * Test the event log is create from the documentSnapshot. We use parameterized testing to check multiple scenarios
     * TODO: Update this test to include expected behaviors
     */
    @OptIn(FireStoreModel::class)
    @ParameterizedTest
    @CsvFileSource(resources = arrayOf("/eventLogVars.csv"), numLinesToSkip = 1)
    fun `test documentSnapshot populates EventLog with parameters`(
        empDocId: String,
        timeRecorded: Long,
        unit: String,
        eventType: String,
        fallbackEmpName: String,
        fallbackEventType: String,
        summary: String,
        description: String
    ) {
        // SETUP
        every { documentSnapshot.getString("employeeDocumentId") } returns empDocId
        every { documentSnapshot.getLong("timeRecorded") } returns timeRecorded
        every { documentSnapshot.getString("unit") } returns unit
        every { documentSnapshot.getString("eventType") } returns eventType
        every { documentSnapshot.getString("fallbackEmployeeName") } returns fallbackEmpName
        every { documentSnapshot.getString("fallbackEventType") } returns fallbackEventType
        every { documentSnapshot.getString("summary") } returns summary
        every { documentSnapshot.getString("description") } returns description


        // ACT
        val eventLog = documentSnapshot.toEventLogRecord()

        // ASSERT
        assertEquals(empDocId, eventLog.employeeDocumentId)
        assertEquals(timeRecorded, eventLog.timeRecorded)
        assertEquals(unit, eventLog.unit)
        assertEquals(EventType.fromString(eventType), eventLog.eventType)
        assertEquals(fallbackEmpName, eventLog.fallbackEmployeeName)
        assertEquals(fallbackEventType, eventLog.fallbackEventType)
        assertEquals(summary, eventLog.summary)
        assertEquals(description, eventLog.description)
    }

    /**
     * Test the time card event log is created from the documentSnapshot
     */
    @OptIn(FireStoreModel::class)
    @Test
    fun `test documentSnapshot populates the TimeCardEvent with details`() {
        // SETUP
        every { documentSnapshot.getString("employeeDocumentId") } returns "empDocId"
        every { documentSnapshot.getString("eventType") } returns "CLOCK_IN"
        every { documentSnapshot.getLong("eventTime") } returns 365443684
        every { documentSnapshot.getString("imageUrl") } returns "image1384384.iam.a.url"

        // ACT
        val timeCardEvent = documentSnapshot.toTimeCardEvent()

        // ASSERT
        assertEquals("empDocId", timeCardEvent.employeeDocumentId)
        assertEquals(TimeCardEventType.CLOCK_IN, timeCardEvent.eventType)
        assertEquals(365443684, timeCardEvent.eventTime)
        assertEquals("image1384384.iam.a.url", timeCardEvent.imageUrl)
    }

    /**
     * Test we create a row from the time card record
     */
    @OptIn(FireStoreModel::class)
    @Test
    fun `test a row is created from a timeCardRecord and matches expected outputs`() {
        // SETUP
        val empDocId: String = "docID"
        val eventType: TimeCardEventType = TimeCardEventType.CLOCK_OUT
        val eventTime: Long = 1715922000
        val imageUrl: String = "imageURL.real.url.here"

        val timeCardRecord = TimeCardRecord(empDocId, eventType, eventTime, imageUrl)

        // ACT
        val rowEntry = timeCardRecord.toRowEntry("Random Name Spanish", "magicURL.I.AM.REAL")

        // ASSERT
        assertEquals("Random Name Spanish", rowEntry[0])
        assertEquals("Salida", rowEntry[1])
        assertEquals("2024-05-17 00:00:00", rowEntry[2])
        assertEquals("magicURL.I.AM.REAL", rowEntry[3])
    }

    /**
     * Test we create an event log record from a set of parameters. Multiple inputs checked
     * TODO: Update this test to handle issues
     */
    @OptIn(FireStoreModel::class)
    @ParameterizedTest
    @CsvFileSource(resources = arrayOf("/eventLogVars.csv"), numLinesToSkip = 1)
    fun `test a row is created form an eventLogRecord and matches expected outputs`(
        empDocId: String,
        timeRecorded: Long,
        unit: String,
        eventType: String,
        fallbackEmpName: String,
        fallbackEventType: String,
        summary: String,
        description: String,
        expectedRowEvent: String
    ) {
        // SETUP
        val eventLogRecord =
            EventLogRecord(
                empDocId,
                timeRecorded,
                unit,
                EventType.fromString(eventType),
                fallbackEmpName,
                fallbackEventType,
                summary,
                description
            )

        // ACT
        val rowEntry = eventLogRecord.toRowEntry("Test Name Rodriguez", "i am a an attachment")

        // ASSERT
        assertEquals("Test Name Rodriguez", rowEntry[0])
        assertEquals("2024-05-17 00:00:00", rowEntry[1])
        assertEquals(unit, rowEntry[2])
        assertEquals(expectedRowEvent, rowEntry[3])
        assertEquals(fallbackEmpName, rowEntry[4])
        assertEquals(fallbackEventType, rowEntry[5])
        assertEquals(summary, rowEntry[6])
        assertEquals(description, rowEntry[7])
        assertEquals("i am a an attachment", rowEntry[8])
    }
}