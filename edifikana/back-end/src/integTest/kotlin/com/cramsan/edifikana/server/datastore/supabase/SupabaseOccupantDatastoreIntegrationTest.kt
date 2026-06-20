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
    fun `createOccupant should return the created record`() = runBlocking {
        val startDate = LocalDate(2026, 1, 1)

        val result = occupantDatastore.createOccupant(
            unitId = unitId!!,
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
        assertEquals("Jane Doe", occupant.name)
        assertEquals("jane@example.com", occupant.email)
        assertEquals(OccupantType.TENANT, occupant.occupantType)
        assertTrue(occupant.isPrimary)
        assertEquals(startDate, occupant.startDate)
        assertNull(occupant.endDate)
        assertEquals(OccupancyStatus.ACTIVE, occupant.status)
    }

    @Test
    fun `getOccupant should return the created record`() = runBlocking {
        val createResult = occupantDatastore.createOccupant(
            unitId = unitId!!,
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
    fun `listOccupantsForUnit should return active occupants only by default`() = runBlocking {
        val unit2Id = createTestUnit(propertyId!!, "${testPrefix}_U2")

        val active1 = occupantDatastore.createOccupant(
            unitId = unit2Id,
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
    fun `listOccupantsForProperty should aggregate occupants across all units and respect includeInactive`() =
        runBlocking {
            val unitA = createTestUnit(propertyId!!, "${testPrefix}_PA")
            val unitB = createTestUnit(propertyId!!, "${testPrefix}_PB")

            // Occupant in another property to confirm cross-property isolation
            val otherOrgId = createTestOrganization("occ_org_other_$testPrefix", "")
            val otherPropertyId = createTestProperty("${testPrefix}_OtherProp", testUserId!!, otherOrgId)
            val otherUnitId = createTestUnit(otherPropertyId, "${testPrefix}_OU1")
            val otherOccupant = occupantDatastore.createOccupant(
                unitId = otherUnitId,
                userId = null,
                addedBy = testUserId,
                name = "Outsider",
                email = null,
                occupantType = OccupantType.TENANT,
                isPrimary = true,
                startDate = LocalDate(2026, 1, 1),
                endDate = null,
            ).registerOccupantForDeletion().getOrThrow()

            val activeA = occupantDatastore.createOccupant(
                unitId = unitA,
                userId = null,
                addedBy = testUserId,
                name = "Frank",
                email = null,
                occupantType = OccupantType.TENANT,
                isPrimary = true,
                startDate = LocalDate(2026, 1, 1),
                endDate = null,
            ).registerOccupantForDeletion().getOrThrow()

            val activeB = occupantDatastore.createOccupant(
                unitId = unitB,
                userId = null,
                addedBy = testUserId,
                name = "Grace",
                email = null,
                occupantType = OccupantType.RESIDENT,
                isPrimary = false,
                startDate = LocalDate(2026, 2, 1),
                endDate = null,
            ).registerOccupantForDeletion().getOrThrow()

            val toDeactivate = occupantDatastore.createOccupant(
                unitId = unitB,
                userId = null,
                addedBy = testUserId,
                name = "Henry",
                email = null,
                occupantType = OccupantType.TENANT,
                isPrimary = false,
                startDate = LocalDate(2026, 1, 1),
                endDate = null,
            ).registerOccupantForDeletion().getOrThrow()

            occupantDatastore.softRemoveOccupant(toDeactivate.id, LocalDate(2026, 3, 1)).getOrThrow()

            val activeOnly = occupantDatastore
                .listOccupantsForProperty(propertyId!!, includeInactive = false)
                .getOrThrow()
            val allOccupants = occupantDatastore
                .listOccupantsForProperty(propertyId!!, includeInactive = true)
                .getOrThrow()

            val activeOnlyIds = activeOnly.map { it.id }.toSet()
            assertEquals(setOf(activeA.id, activeB.id), activeOnlyIds)
            assertFalse(activeOnlyIds.contains(otherOccupant.id))

            val allIds = allOccupants.map { it.id }.toSet()
            assertEquals(setOf(activeA.id, activeB.id, toDeactivate.id), allIds)
            assertFalse(allIds.contains(otherOccupant.id))
        }

    @Test
    fun `listOccupantsForProperty should return empty list when property has no units`() = runBlocking {
        val emptyOrgId = createTestOrganization("occ_org_empty_$testPrefix", "")
        val emptyPropertyId = createTestProperty("${testPrefix}_EmptyProp", testUserId!!, emptyOrgId)

        val result = occupantDatastore.listOccupantsForProperty(emptyPropertyId, includeInactive = false)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    @Test
    fun `clearPrimaryForUnit should unset is_primary on all active occupants`() = runBlocking {
        val unit3Id = createTestUnit(propertyId!!, "${testPrefix}_U3")

        occupantDatastore.createOccupant(
            unitId = unit3Id,
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
    fun `softRemoveOccupant should set status to INACTIVE without deleting the row`() = runBlocking {
        val createResult = occupantDatastore.createOccupant(
            unitId = unitId!!,
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
    fun `updateOccupant should apply only provided fields`() = runBlocking {
        val created = occupantDatastore.createOccupant(
            unitId = unitId!!,
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
            name = "Eve Updated",
            email = "eve@example.com",
            occupantType = OccupantType.RESIDENT,
            isPrimary = null,
            endDate = null,
            status = null,
        ).getOrThrow()

        assertEquals("Eve Updated", updated.name)
        assertEquals("eve@example.com", updated.email)
        assertEquals(OccupantType.RESIDENT, updated.occupantType)
        assertFalse(updated.isPrimary)
        assertEquals(OccupancyStatus.ACTIVE, updated.status)
    }
}
