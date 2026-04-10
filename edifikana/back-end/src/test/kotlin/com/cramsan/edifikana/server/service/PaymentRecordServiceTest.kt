@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.server.datastore.PaymentRecordDatastore
import com.cramsan.edifikana.server.service.models.PaymentRecord
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Test class for [PaymentRecordService].
 */
@OptIn(ExperimentalTime::class)
class PaymentRecordServiceTest {

    private lateinit var paymentRecordDatastore: PaymentRecordDatastore
    private lateinit var paymentRecordService: PaymentRecordService

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        paymentRecordDatastore = mockk()
        paymentRecordService = PaymentRecordService(paymentRecordDatastore)
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    // -------------------------------------------------------------------------
    // createPaymentRecord
    // -------------------------------------------------------------------------

    /**
     * Tests that createPaymentRecord delegates to the datastore and returns the created record.
     */
    @Test
    fun `createPaymentRecord should delegate to datastore and return created record`() = runTest {
        // Arrange
        val unitId = UnitId("unit123")
        val periodMonth = LocalDate(2026, 3, 1)
        val record = paymentRecord(PaymentRecordId("pr123"), unitId)
        coEvery {
            paymentRecordDatastore.createPaymentRecord(
                unitId = unitId,
                paymentType = PaymentType.RENT,
                periodMonth = periodMonth,
                amountDue = 120000L,
                dueDate = null,
                recordedBy = null,
                notes = null,
            )
        } returns Result.success(record)

        // Act
        val result = paymentRecordService.createPaymentRecord(
            unitId = unitId,
            paymentType = PaymentType.RENT,
            periodMonth = periodMonth,
            amountDue = 120000L,
            dueDate = null,
            recordedBy = null,
            notes = null,
        )

        // Assert
        assertEquals(record, result)
        coVerify {
            paymentRecordDatastore.createPaymentRecord(
                unitId = unitId,
                paymentType = PaymentType.RENT,
                periodMonth = periodMonth,
                amountDue = 120000L,
                dueDate = null,
                recordedBy = null,
                notes = null,
            )
        }
    }

    // -------------------------------------------------------------------------
    // getPaymentRecord
    // -------------------------------------------------------------------------

    /**
     * Tests that getPaymentRecord returns the record when found.
     */
    @Test
    fun `getPaymentRecord should return record when found`() = runTest {
        // Arrange
        val id = PaymentRecordId("pr123")
        val record = paymentRecord(id, UnitId("unit123"))
        coEvery { paymentRecordDatastore.getPaymentRecord(id) } returns Result.success(record)

        // Act
        val result = paymentRecordService.getPaymentRecord(id)

        // Assert
        assertEquals(record, result)
    }

    /**
     * Tests that getPaymentRecord returns null when the record is not found.
     */
    @Test
    fun `getPaymentRecord should return null when not found`() = runTest {
        // Arrange
        val id = PaymentRecordId("pr123")
        coEvery { paymentRecordDatastore.getPaymentRecord(id) } returns Result.success(null)

        // Act
        val result = paymentRecordService.getPaymentRecord(id)

        // Assert
        assertNull(result)
    }

    // -------------------------------------------------------------------------
    // listPaymentRecords
    // -------------------------------------------------------------------------

    /**
     * Tests that listPaymentRecords returns all records for a unit when no period filter is applied.
     */
    @Test
    fun `listPaymentRecords should return list from datastore without filter`() = runTest {
        // Arrange
        val unitId = UnitId("unit123")
        val records = listOf(
            paymentRecord(PaymentRecordId("pr123"), unitId),
            paymentRecord(PaymentRecordId("pr456"), unitId),
        )
        coEvery {
            paymentRecordDatastore.listPaymentRecords(unitId, null)
        } returns Result.success(records)

        // Act
        val result = paymentRecordService.listPaymentRecords(unitId, null)

        // Assert
        assertEquals(records, result)
    }

    /**
     * Tests that listPaymentRecords passes the periodMonth filter to the datastore.
     */
    @Test
    fun `listPaymentRecords should pass periodMonth filter to datastore`() = runTest {
        // Arrange
        val unitId = UnitId("unit123")
        val periodMonth = "2026-03"
        val records = listOf(paymentRecord(PaymentRecordId("pr123"), unitId))
        coEvery {
            paymentRecordDatastore.listPaymentRecords(unitId, periodMonth)
        } returns Result.success(records)

        // Act
        val result = paymentRecordService.listPaymentRecords(unitId, periodMonth)

        // Assert
        assertEquals(records, result)
        coVerify { paymentRecordDatastore.listPaymentRecords(unitId, periodMonth) }
    }

    // -------------------------------------------------------------------------
    // updatePaymentRecord
    // -------------------------------------------------------------------------

    /**
     * Tests that updatePaymentRecord delegates to the datastore and returns the updated record.
     */
    @Test
    fun `updatePaymentRecord should delegate to datastore and return updated record`() = runTest {
        // Arrange
        val id = PaymentRecordId("pr123")
        val amountPaid = 120000L
        val paidDate = LocalDate(2026, 3, 15)
        val status = PaymentStatus.PAID
        val updatedRecord = paymentRecord(id, UnitId("unit123"), status = PaymentStatus.PAID, amountPaid = amountPaid)
        coEvery {
            paymentRecordDatastore.updatePaymentRecord(id, amountPaid, paidDate, status, null)
        } returns Result.success(updatedRecord)

        // Act
        val result = paymentRecordService.updatePaymentRecord(id, amountPaid, paidDate, status, null)

        // Assert
        assertEquals(updatedRecord, result)
        coVerify { paymentRecordDatastore.updatePaymentRecord(id, amountPaid, paidDate, status, null) }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun paymentRecord(
        id: PaymentRecordId,
        unitId: UnitId,
        status: PaymentStatus = PaymentStatus.PENDING,
        amountPaid: Long? = null,
    ) = PaymentRecord(
        id = id,
        unitId = unitId,
        paymentType = PaymentType.RENT,
        periodMonth = LocalDate(2026, 3, 1),
        amountDue = 120000L,
        amountPaid = amountPaid,
        status = status,
        dueDate = null,
        paidDate = null,
        recordedBy = UserId("user123"),
        recordedAt = Instant.fromEpochMilliseconds(0),
        notes = null,
    )
}
