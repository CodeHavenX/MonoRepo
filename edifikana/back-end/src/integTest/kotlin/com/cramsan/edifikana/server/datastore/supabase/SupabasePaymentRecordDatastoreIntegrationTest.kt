package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class SupabasePaymentRecordDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var testPrefix: String
    private var propertyId: PropertyId? = null
    private var testUserId: UserId? = null
    private var orgId: OrganizationId? = null
    private var unitId: UnitId? = null

    @BeforeTest
    fun setup() {
        testPrefix = UUID.random()
        runBlocking {
            testUserId = createTestUser("user-${testPrefix}@test.com")
            orgId = createTestOrganization("org_$testPrefix", "")
            propertyId = createTestProperty("${testPrefix}_Property", testUserId!!, orgId!!)
            unitId = createTestUnit(propertyId!!, "${testPrefix}_101")
        }
    }

    @Test
    fun `createPaymentRecord should return created record`() = runCoroutineTest {
        // Arrange
        val periodMonth = LocalDate(2026, 3, 1)

        // Act
        val result = paymentRecordDatastore.createPaymentRecord(
            unitId = unitId!!,
            paymentType = PaymentType.RENT,
            periodMonth = periodMonth,
            amountDue = 120000.0,
            dueDate = null,
            recordedBy = testUserId,
            notes = null,
        ).registerPaymentRecordForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val record = result.getOrNull()
        assertNotNull(record)
        assertEquals(unitId, record.unitId)
        assertEquals(PaymentType.RENT, record.paymentType)
        assertEquals(periodMonth, record.periodMonth)
        assertEquals(120000.0, record.amountDue)
        assertEquals(PaymentStatus.PENDING, record.status)
    }

    @Test
    fun `getPaymentRecord should return created record`() = runCoroutineTest {
        // Arrange
        val createResult = paymentRecordDatastore.createPaymentRecord(
            unitId = unitId!!,
            paymentType = PaymentType.RENT,
            periodMonth = LocalDate(2026, 3, 1),
            amountDue = 120000.0,
            dueDate = null,
            recordedBy = testUserId,
            notes = null,
        ).registerPaymentRecordForDeletion()
        assertTrue(createResult.isSuccess)
        val created = createResult.getOrNull()!!

        // Act
        val getResult = paymentRecordDatastore.getPaymentRecord(created.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertEquals(created.id, fetched.id)
        assertEquals(unitId, fetched.unitId)
    }

    @Test
    fun `getPaymentRecord should return null when not found`() = runCoroutineTest {
        // Arrange
        val fakeId = PaymentRecordId(UUID.random())

        // Act
        val result = paymentRecordDatastore.getPaymentRecord(fakeId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `listPaymentRecords should return all records for unit without filter`() = runCoroutineTest {
        // Arrange

        // Act
        val result1 = paymentRecordDatastore.createPaymentRecord(
            unitId = unitId!!,
            paymentType = PaymentType.RENT,
            periodMonth = LocalDate(2026, 3, 1),
            amountDue = 120000.0,
            dueDate = null,
            recordedBy = testUserId,
            notes = null,
        ).registerPaymentRecordForDeletion()
        val result2 = paymentRecordDatastore.createPaymentRecord(
            unitId = unitId!!,
            paymentType = PaymentType.RENT,
            periodMonth = LocalDate(2026, 4, 1),
            amountDue = 120000.0,
            dueDate = null,
            recordedBy = testUserId,
            notes = null,
        ).registerPaymentRecordForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        val listResult = paymentRecordDatastore.listPaymentRecords(unitId!!, null)

        // Assert
        assertTrue(listResult.isSuccess)
        val records = listResult.getOrNull()
        assertNotNull(records)
        assertTrue(records.size >= 2)
    }

    @Test
    fun `listPaymentRecords should filter by periodMonth`() = runCoroutineTest {
        // Arrange

        // Act
        val marchResult = paymentRecordDatastore.createPaymentRecord(
            unitId = unitId!!,
            paymentType = PaymentType.RENT,
            periodMonth = LocalDate(2026, 3, 1),
            amountDue = 120000.0,
            dueDate = null,
            recordedBy = testUserId,
            notes = "${testPrefix}_march",
        ).registerPaymentRecordForDeletion()
        val aprilResult = paymentRecordDatastore.createPaymentRecord(
            unitId = unitId!!,
            paymentType = PaymentType.RENT,
            periodMonth = LocalDate(2026, 4, 1),
            amountDue = 120000.0,
            dueDate = null,
            recordedBy = testUserId,
            notes = "${testPrefix}_april",
        ).registerPaymentRecordForDeletion()
        assertTrue(marchResult.isSuccess)
        assertTrue(aprilResult.isSuccess)
        val listResult = paymentRecordDatastore.listPaymentRecords(unitId!!, "2026-03")

        // Assert
        assertTrue(listResult.isSuccess)
        val records = listResult.getOrNull()
        assertNotNull(records)
        val notes = records.map { it.notes }
        assertTrue(notes.contains("${testPrefix}_march"))
        assertTrue(!notes.contains("${testPrefix}_april"))
    }

    @Test
    fun `updatePaymentRecord should update status and amount paid`() = runCoroutineTest {
        // Arrange
        val createResult = paymentRecordDatastore.createPaymentRecord(
            unitId = unitId!!,
            paymentType = PaymentType.RENT,
            periodMonth = LocalDate(2026, 3, 1),
            amountDue = 120000.0,
            dueDate = null,
            recordedBy = testUserId,
            notes = null,
        ).registerPaymentRecordForDeletion()
        assertTrue(createResult.isSuccess)
        val record = createResult.getOrNull()!!

        // Act
        val updateResult = paymentRecordDatastore.updatePaymentRecord(
            paymentRecordId = record.id,
            amountPaid = 120000.0,
            paidDate = LocalDate(2026, 3, 15),
            status = PaymentStatus.PAID,
            notes = null,
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertEquals(120000.0, updated.amountPaid)
        assertEquals(PaymentStatus.PAID, updated.status)
        assertEquals(LocalDate(2026, 3, 15), updated.paidDate)
    }

    @Test
    fun `deletePaymentRecord should soft delete and make record invisible`() = runCoroutineTest {
        // Arrange
        val createResult = paymentRecordDatastore.createPaymentRecord(
            unitId = unitId!!,
            paymentType = PaymentType.RENT,
            periodMonth = LocalDate(2026, 5, 1),
            amountDue = 120000.0,
            dueDate = null,
            recordedBy = testUserId,
            notes = null,
        ).registerPaymentRecordForDeletion()
        assertTrue(createResult.isSuccess)
        val record = createResult.getOrNull()!!

        // Act
        val deleteResult = paymentRecordDatastore.deletePaymentRecord(record.id)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = paymentRecordDatastore.getPaymentRecord(record.id)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `purgePaymentRecord should hard delete the record`() = runCoroutineTest {
        // Arrange
        val createResult = paymentRecordDatastore.createPaymentRecord(
            unitId = unitId!!,
            paymentType = PaymentType.RENT,
            periodMonth = LocalDate(2026, 6, 1),
            amountDue = 120000.0,
            dueDate = null,
            recordedBy = testUserId,
            notes = null,
        )
        assertTrue(createResult.isSuccess)
        val record = createResult.getOrNull()!!

        // Act
        val purgeResult = paymentRecordDatastore.purgePaymentRecord(record.id)

        // Assert
        assertTrue(purgeResult.isSuccess)
        val getResult = paymentRecordDatastore.getPaymentRecord(record.id)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

}
