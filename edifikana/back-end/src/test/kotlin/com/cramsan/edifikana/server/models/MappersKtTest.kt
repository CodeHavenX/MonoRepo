package com.cramsan.edifikana.server.models

import com.cramsan.edifikana.lib.firestore.*
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
     *
     */
    @BeforeEach
    fun setUp() {
        documentSnapshot = mockk<DocumentSnapshot>()
    }

    @AfterEach
    fun tearDown() {
    }

    @OptIn(FireStoreModel::class)
    @Test
    fun toEmployee() {
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

    @OptIn(FireStoreModel::class)
    @ParameterizedTest
    @CsvFileSource(resources = arrayOf("/eventLogVars.csv"), numLinesToSkip = 1)
    fun toEventLogRecord(
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

    @OptIn(FireStoreModel::class)
    @Test
    fun toTimeCardEvent() {
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

    @Test
    fun toRowEntry() {
        // SETUP

        // ACT

        // ASSERT
    }

    @Test
    fun testToRowEntry() {
        // SETUP

        // ACT

        // ASSERT
    }
}