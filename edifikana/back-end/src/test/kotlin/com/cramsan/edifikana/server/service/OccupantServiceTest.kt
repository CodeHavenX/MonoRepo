@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.server.service

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.server.datastore.OccupantDatastore
import com.cramsan.edifikana.server.datastore.UnitDatastore
import com.cramsan.edifikana.server.service.models.Occupant
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.ConflictException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.assertFailsWith
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class OccupantServiceTest {

    private lateinit var occupantDatastore: OccupantDatastore
    private lateinit var unitDatastore: UnitDatastore
    private lateinit var clock: Clock
    private lateinit var occupantService: OccupantService

    private val unitId = UnitId("unit-1")
    private val orgId = OrganizationId("org-1")
    private val occupantId = OccupantId("occ-1")

    @BeforeEach
    fun setUp() {
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
        occupantDatastore = mockk()
        unitDatastore = mockk()
        clock = mockk()
        every { clock.now() } returns Instant.fromEpochSeconds(1_700_000_000)
        occupantService = OccupantService(occupantDatastore, unitDatastore, clock)
    }

    @AfterTest
    fun cleanUp() {
        stopKoin()
    }

    private fun makeOccupant(
        id: OccupantId = occupantId,
        isPrimary: Boolean = false,
        status: OccupancyStatus = OccupancyStatus.ACTIVE,
    ): Occupant = Occupant(
        id = id,
        unitId = unitId,
        userId = null,
        addedBy = null,
        occupantType = OccupantType.TENANT,
        isPrimary = isPrimary,
        startDate = LocalDate(2026, 1, 1),
        endDate = null,
        status = status,
        addedAt = Instant.fromEpochSeconds(1_700_000_000),
    )

    // -------------------------------------------------------------------------
    // removeOccupant — 409 guard
    // -------------------------------------------------------------------------

    @Test
    fun `removeOccupant throws ConflictException when removing primary while other active occupants exist`() = runTest {
        val primary = makeOccupant(isPrimary = true)
        val other = makeOccupant(id = OccupantId("occ-2"), isPrimary = false)

        coEvery { occupantDatastore.getOccupant(occupantId) } returns Result.success(primary)
        coEvery {
            occupantDatastore.listOccupantsForUnit(unitId, includeInactive = false)
        } returns Result.success(listOf(primary, other))

        assertFailsWith<ConflictException> {
            occupantService.removeOccupant(occupantId)
        }
    }

    @Test
    fun `removeOccupant succeeds when primary is the only active occupant`() = runTest {
        val primary = makeOccupant(isPrimary = true)
        val removed = makeOccupant(isPrimary = true, status = OccupancyStatus.INACTIVE)

        coEvery { occupantDatastore.getOccupant(occupantId) } returns Result.success(primary)
        coEvery {
            occupantDatastore.listOccupantsForUnit(unitId, includeInactive = false)
        } returns Result.success(listOf(primary))
        coEvery {
            occupantDatastore.softRemoveOccupant(occupantId, any())
        } returns Result.success(removed)

        val result = occupantService.removeOccupant(occupantId)
        coVerify { occupantDatastore.softRemoveOccupant(occupantId, any()) }
        assert(result.status == OccupancyStatus.INACTIVE)
    }

    @Test
    fun `removeOccupant succeeds for a non-primary occupant without checking active count`() = runTest {
        val nonPrimary = makeOccupant(isPrimary = false)
        val removed = makeOccupant(isPrimary = false, status = OccupancyStatus.INACTIVE)

        coEvery { occupantDatastore.getOccupant(occupantId) } returns Result.success(nonPrimary)
        coEvery {
            occupantDatastore.softRemoveOccupant(occupantId, any())
        } returns Result.success(removed)

        occupantService.removeOccupant(occupantId)
        coVerify { occupantDatastore.softRemoveOccupant(occupantId, any()) }
    }

    // -------------------------------------------------------------------------
    // updateOccupant — unset-primary guard
    // -------------------------------------------------------------------------

    @Test
    fun `updateOccupant throws ConflictException when unsetting primary on the only active occupant`() = runTest {
        val existing = makeOccupant(isPrimary = true)

        coEvery { occupantDatastore.getOccupant(occupantId) } returns Result.success(existing)
        coEvery {
            occupantDatastore.listOccupantsForUnit(unitId, includeInactive = false)
        } returns Result.success(listOf(existing))

        assertFailsWith<ConflictException> {
            occupantService.updateOccupant(
                occupantId = occupantId,
                occupantType = null,
                isPrimary = false,
                endDate = null,
                status = null,
            )
        }
    }

    @Test
    fun `updateOccupant allows unsetting primary when multiple active occupants exist`() = runTest {
        val existing = makeOccupant(isPrimary = true)
        val other = makeOccupant(id = OccupantId("occ-2"), isPrimary = false)
        val updated = makeOccupant(isPrimary = false)

        coEvery { occupantDatastore.getOccupant(occupantId) } returns Result.success(existing)
        coEvery {
            occupantDatastore.listOccupantsForUnit(unitId, includeInactive = false)
        } returns Result.success(listOf(existing, other))
        coEvery {
            occupantDatastore.updateOccupant(occupantId, null, false, null, null)
        } returns Result.success(updated)

        val result = occupantService.updateOccupant(
            occupantId = occupantId,
            occupantType = null,
            isPrimary = false,
            endDate = null,
            status = null,
        )
        assert(!result.isPrimary)
    }

    @Test
    fun `updateOccupant clears primary for unit when setting isPrimary to true`() = runTest {
        val existing = makeOccupant(isPrimary = false)
        val updated = makeOccupant(isPrimary = true)

        coEvery { occupantDatastore.getOccupant(occupantId) } returns Result.success(existing)
        coEvery { occupantDatastore.clearPrimaryForUnit(unitId) } returns Result.success(Unit)
        coEvery {
            occupantDatastore.updateOccupant(occupantId, null, true, null, null)
        } returns Result.success(updated)

        val result = occupantService.updateOccupant(
            occupantId = occupantId,
            occupantType = null,
            isPrimary = true,
            endDate = null,
            status = null,
        )
        coVerify { occupantDatastore.clearPrimaryForUnit(unitId) }
        assert(result.isPrimary)
    }
}
