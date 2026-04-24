package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabaseOccupantDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var testPrefix: String
    private var propertyId: PropertyId? = null
    private var testUserId: UserId? = null
    private var orgId: OrganizationId? = null
    private var unitId: UnitId? = null

    @BeforeTest
    fun setup() {
        testPrefix = UUID.random()
        runBlocking {
            testUserId = createTestUser("occ-user-${testPrefix}@test.com")
            orgId = createTestOrganization("occ_org_$testPrefix", "")
            propertyId = createTestProperty("${testPrefix}_OccProp", testUserId!!, orgId!!)
            unitId = createTestUnit(propertyId!!, "${testPrefix}_U1")
        }
    }

    @Test
    fun `createOccupant should return the created record`() = runCoroutineTest {
        val startDate = LocalDate(2026, 1, 1)

        val result = occupantDatastore.createOccupant(
            unitId = unitId!!,
            orgId = orgId!!,
            userId = testUserId,
            addedBy = testUserId,
            name = "Jane Doe",
            email = "jane@example.com",
            occupantType = OccupantType.TENANT,
            isPrimary = true,
            startDate = startDate,
            endDate = null,
        ).registerOccupantForDeletion()

        assertTrue(result.isSuccess)
        val occupant = result.getOrNull()
        assertNotNull(occupant)
        assertEquals(unitId, occupant.unitId)
        assertEquals(testUserId, occupant.userId)
        assertEquals(OccupantType.TENANT, occupant.occupantType)
        assertTrue(occupant.isPrimary)
        assertEquals(startDate, occupant.startDate)
        assertNull(occupant.endDate)
        assertEquals(OccupancyStatus.ACTIVE, occupant.status)
    }

    @Test
    fun `getOccupant should return the created record`() = runCoroutineTest {
        val createResult = occupantDatastore.createOccupant(
            unitId = unitId!!,
            orgId = orgId!!,
            userId = testUserId,
            addedBy = testUserId,
            name = "John Smith",
            email = null,
            occupantType = OccupantType.TENANT,
            isPrimary = false,
            startDate = LocalDate(2026, 1, 1),
            endDate = null,
        ).registerOccupantForDeletion()
        assertTrue(createResult.isSuccess)
        val created = createResult.getOrNull()!!

        val getResult = occupantDatastore.getOccupant(created.id)

        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertEquals(created.id, fetched.id)
        assertEquals(unitId, fetched.unitId)
    }

    @Test
    fun `listOccupantsForUnit should return active occupants only by default`() = runCoroutineTest {
        val unit2Id = createTestUnit(propertyId!!, "${testPrefix}_U2")

        val active1 = occupantDatastore.createOccupant(
            unitId = unit2Id,
            orgId = orgId!!,
            userId = null,
            addedBy = testUserId,
            name = "Alice",
            email = null,
            occupantType = OccupantType.TENANT,
            isPrimary = true,
            startDate = LocalDate(2026, 1, 1),
            endDate = null,
        ).registerOccupantForDeletion().getOrThrow()

        val active2 = occupantDatastore.createOccupant(
            unitId = unit2Id,
            orgId = orgId!!,
            userId = null,
            addedBy = testUserId,
            name = "Bob",
            email = null,
            occupantType = OccupantType.RESIDENT,
            isPrimary = false,
            startDate = LocalDate(2026, 2, 1),
            endDate = null,
        ).registerOccupantForDeletion().getOrThrow()

        // Soft-remove one occupant
        occupantDatastore.softRemoveOccupant(active2.id, LocalDate(2026, 3, 1)).getOrThrow()

        val activeOnly = occupantDatastore.listOccupantsForUnit(unit2Id, includeInactive = false).getOrThrow()
        val allOccupants = occupantDatastore.listOccupantsForUnit(unit2Id, includeInactive = true).getOrThrow()

        assertEquals(1, activeOnly.size)
        assertEquals(active1.id, activeOnly.first().id)

        assertEquals(2, allOccupants.size)
    }

    @Test
    fun `clearPrimaryForUnit should unset is_primary on all active occupants`() = runCoroutineTest {
        val unit3Id = createTestUnit(propertyId!!, "${testPrefix}_U3")

        occupantDatastore.createOccupant(
            unitId = unit3Id,
            orgId = orgId!!,
            userId = null,
            addedBy = testUserId,
            name = "Carol",
            email = null,
            occupantType = OccupantType.TENANT,
            isPrimary = true,
            startDate = LocalDate(2026, 1, 1),
            endDate = null,
        ).registerOccupantForDeletion().getOrThrow()

        occupantDatastore.clearPrimaryForUnit(unit3Id).getOrThrow()

        val occupants = occupantDatastore.listOccupantsForUnit(unit3Id, includeInactive = false).getOrThrow()
        assertTrue(occupants.all { !it.isPrimary })
    }

    @Test
    fun `softRemoveOccupant should set status to INACTIVE without deleting the row`() = runCoroutineTest {
        val createResult = occupantDatastore.createOccupant(
            unitId = unitId!!,
            orgId = orgId!!,
            userId = null,
            addedBy = testUserId,
            name = "Dave",
            email = null,
            occupantType = OccupantType.TENANT,
            isPrimary = false,
            startDate = LocalDate(2026, 1, 1),
            endDate = null,
        ).registerOccupantForDeletion()
        val created = createResult.getOrThrow()

        val today = LocalDate(2026, 4, 23)
        val removeResult = occupantDatastore.softRemoveOccupant(created.id, today)

        assertTrue(removeResult.isSuccess)
        val removed = removeResult.getOrNull()
        assertNotNull(removed)
        assertEquals(OccupancyStatus.INACTIVE, removed.status)
        assertEquals(today, removed.endDate)

        // Row still exists (soft-deleted, not hard-deleted)
        val fetched = occupantDatastore.getOccupant(created.id).getOrNull()
        assertNotNull(fetched)
        assertEquals(OccupancyStatus.INACTIVE, fetched.status)
    }

    @Test
    fun `updateOccupant should apply only provided fields`() = runCoroutineTest {
        val created = occupantDatastore.createOccupant(
            unitId = unitId!!,
            orgId = orgId!!,
            userId = null,
            addedBy = testUserId,
            name = "Eve",
            email = null,
            occupantType = OccupantType.TENANT,
            isPrimary = false,
            startDate = LocalDate(2026, 1, 1),
            endDate = null,
        ).registerOccupantForDeletion().getOrThrow()

        val updated = occupantDatastore.updateOccupant(
            occupantId = created.id,
            occupantType = OccupantType.RESIDENT,
            isPrimary = null,
            endDate = null,
            status = null,
        ).getOrThrow()

        assertEquals(OccupantType.RESIDENT, updated.occupantType)
        assertFalse(updated.isPrimary)
        assertEquals(OccupancyStatus.ACTIVE, updated.status)
    }
}
