package com.cramsan.flyerboard.client.lib.service.impl

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import com.cramsan.flyerboard.lib.model.network.FlyerNetworkResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FlyerNetworkMapperTest {
    @Test
    fun `REJECTED response with rejection_reason maps rejectionReason`() {
        val response =
            makeResponse(
                status = FlyerStatus.REJECTED,
                rejectionReason = "Inappropriate",
            )

        val model = response.toFlyerModel()

        assertEquals("Inappropriate", model.rejectionReason)
    }

    @Test
    fun `REJECTED response with null rejection_reason maps null rejectionReason`() {
        val response =
            makeResponse(
                status = FlyerStatus.REJECTED,
                rejectionReason = null,
            )

        val model = response.toFlyerModel()

        assertNull(model.rejectionReason)
    }

    @Test
    fun `APPROVED response with null rejection_reason maps null rejectionReason`() {
        val response =
            makeResponse(
                status = FlyerStatus.APPROVED,
                rejectionReason = null,
            )

        val model = response.toFlyerModel()

        assertNull(model.rejectionReason)
    }

    private fun makeResponse(status: FlyerStatus, rejectionReason: String?) =
        FlyerNetworkResponse(
            id = FlyerId("flyer-1"),
            title = "Test Flyer",
            description = "Description",
            fileUrl = null,
            status = status,
            expiresAt = null,
            uploaderId = UserId("user-1"),
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            rejectionReason = rejectionReason,
        )
}
