@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.controller

import com.cramsan.architecture.server.test.startTestKoin
import com.cramsan.architecture.server.test.testBackEndApplication
import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.lib.model.payment.PaymentStatus
import com.cramsan.edifikana.lib.model.payment.PaymentType
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.dependencyinjection.TestControllerModule
import com.cramsan.edifikana.server.dependencyinjection.TestServiceModule
import com.cramsan.edifikana.server.dependencyinjection.testApplicationModule
import com.cramsan.edifikana.server.service.PaymentRecordService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.PaymentRecord
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.edifikana.server.utils.readFileContent
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.test.CoroutineTest
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.datetime.LocalDate
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PaymentRecordControllerTest : CoroutineTest(), KoinTest {

    @BeforeTest
    fun setupTest() {
        startTestKoin(
            testApplicationModule(createJson()),
            TestControllerModule,
            TestServiceModule,
        )
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    // -------------------------------------------------------------------------
    // createPaymentRecord
    // -------------------------------------------------------------------------

    @Test
    fun `test createPaymentRecord succeeds when user has ADMIN role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_payment_record_request.json")
        val expectedResponse = readFileContent("requests/create_payment_record_response.json")
        val paymentRecordService = get<PaymentRecordService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, unitId, UserRole.ADMIN) } returns true
        coEvery {
            paymentRecordService.createPaymentRecord(
                unitId = unitId,
                paymentType = PaymentType.RENT,
                periodMonth = LocalDate(2026, 3, 1),
                amountDue = 120000L,
                dueDate = null,
                recordedBy = UserId("user123"),
                notes = null,
            )
        }.answers { paymentRecord(PaymentRecordId("pr123"), unitId) }

        // Act
        val response = client.post("payment-records") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test createPaymentRecord fails when user lacks ADMIN role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/create_payment_record_request.json")
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val paymentRecordService = get<PaymentRecordService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, unitId, UserRole.ADMIN) } returns false

        // Act
        val response = client.post("payment-records") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { paymentRecordService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // getPaymentRecord
    // -------------------------------------------------------------------------

    @Test
    fun `test getPaymentRecord returns 200 when found and user has EMPLOYEE role`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/get_payment_record_response.json")
        val paymentRecordService = get<PaymentRecordService>()
        val rbacService = get<RBACService>()
        val id = PaymentRecordId("pr123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, id, UserRole.EMPLOYEE) } returns true
        coEvery { paymentRecordService.getPaymentRecord(id) }.answers {
            paymentRecord(id, UnitId("unit123"))
        }

        // Act
        val response = client.get("payment-records/pr123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test getPaymentRecord returns 404 when record not found`() = testBackEndApplication {
        // Arrange
        val paymentRecordService = get<PaymentRecordService>()
        val rbacService = get<RBACService>()
        val id = PaymentRecordId("pr123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, id, UserRole.EMPLOYEE) } returns true
        coEvery { paymentRecordService.getPaymentRecord(id) } returns null

        // Act
        val response = client.get("payment-records/pr123")

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test getPaymentRecord returns 404 when user is unauthorized`() = testBackEndApplication {
        // Arrange
        val paymentRecordService = get<PaymentRecordService>()
        val rbacService = get<RBACService>()
        val id = PaymentRecordId("pr123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, id, UserRole.EMPLOYEE) } returns false

        // Act
        val response = client.get("payment-records/pr123")

        // Assert
        coVerify { paymentRecordService wasNot Called }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    // -------------------------------------------------------------------------
    // listPaymentRecords
    // -------------------------------------------------------------------------

    @Test
    fun `test listPaymentRecords returns 200 when user has EMPLOYEE role`() = testBackEndApplication {
        // Arrange
        val expectedResponse = readFileContent("requests/list_payment_records_response.json")
        val paymentRecordService = get<PaymentRecordService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, unitId, UserRole.EMPLOYEE) } returns true
        coEvery { paymentRecordService.listPaymentRecords(unitId, null) }.answers {
            listOf(paymentRecord(PaymentRecordId("pr123"), unitId))
        }

        // Act
        val response = client.get("payment-records?unit_id=unit123")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test listPaymentRecords fails when user is unauthorized`() = testBackEndApplication {
        // Arrange
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val paymentRecordService = get<PaymentRecordService>()
        val rbacService = get<RBACService>()
        val unitId = UnitId("unit123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, unitId, UserRole.EMPLOYEE) } returns false

        // Act
        val response = client.get("payment-records?unit_id=unit123")

        // Assert
        coVerify { paymentRecordService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // updatePaymentRecord
    // -------------------------------------------------------------------------

    @Test
    fun `test updatePaymentRecord succeeds when user has ADMIN role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_payment_record_request.json")
        val expectedResponse = readFileContent("requests/update_payment_record_response.json")
        val paymentRecordService = get<PaymentRecordService>()
        val rbacService = get<RBACService>()
        val id = PaymentRecordId("pr123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, id, UserRole.ADMIN) } returns true
        coEvery {
            paymentRecordService.updatePaymentRecord(
                paymentRecordId = id,
                amountPaid = 120000L,
                paidDate = LocalDate(2026, 3, 15),
                status = PaymentStatus.PAID,
                notes = null,
            )
        }.answers {
            paymentRecord(id, UnitId("unit123"), status = PaymentStatus.PAID, amountPaid = 120000L, paidDate = LocalDate(2026, 3, 15))
        }

        // Act
        val response = client.put("payment-records/pr123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    @Test
    fun `test updatePaymentRecord fails when user lacks ADMIN role`() = testBackEndApplication {
        // Arrange
        val requestBody = readFileContent("requests/update_payment_record_request.json")
        val expectedResponse = "You are not authorized to perform this action in your organization."
        val paymentRecordService = get<PaymentRecordService>()
        val rbacService = get<RBACService>()
        val id = PaymentRecordId("pr123")
        val contextRetriever = get<ContextRetriever<SupabaseContextPayload>>()
        val context = ClientContext.AuthenticatedClientContext(
            SupabaseContextPayload(userInfo = mockk(), userId = UserId("user123"))
        )
        coEvery { contextRetriever.getContext(any()) } returns context
        coEvery { rbacService.hasRoleOrHigher(context, id, UserRole.ADMIN) } returns false

        // Act
        val response = client.put("payment-records/pr123") {
            setBody(requestBody)
            contentType(ContentType.Application.Json)
        }

        // Assert
        coVerify { paymentRecordService wasNot Called }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedResponse, response.bodyAsText())
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun paymentRecord(
        id: PaymentRecordId,
        unitId: UnitId,
        status: PaymentStatus = PaymentStatus.PENDING,
        amountPaid: Long? = null,
        paidDate: LocalDate? = null,
    ) = PaymentRecord(
        id = id,
        unitId = unitId,
        paymentType = PaymentType.RENT,
        periodMonth = LocalDate(2026, 3, 1),
        amountDue = 120000L,
        amountPaid = amountPaid,
        status = status,
        dueDate = null,
        paidDate = paidDate,
        recordedBy = UserId("user123"),
        recordedAt = Instant.fromEpochMilliseconds(0),
        notes = null,
    )
}
