package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.utils.uuid.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabaseUnitDatastoreIntegrationTest : SupabaseIntegrationTest() {
    private lateinit var test_prefix: String
    private var testUserId: UserId? = null
    private var testOrg: OrganizationId? = null
    private var testProperty: PropertyId? = null

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
        testUserId = createTestUser("user-${test_prefix}@test.com")
        testOrg = createTestOrganization("org-${test_prefix}", "")
        testProperty = createTestProperty("property-${test_prefix}", testUserId!!, testOrg!!)
    }

    @Test
    fun `createUnit should return unit on success`() = runCoroutineTest {
        // Act
        val result = unitDatastore.createUnit(
            propertyId = testProperty!!,
            unitNumber = "${test_prefix}_101",
            bedrooms = 2,
            bathrooms = 1,
            sqFt = 750,
            floor = 1,
            notes = "Test unit",
        ).registerUnitForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val unit = result.getOrNull()
        assertNotNull(unit)
        assertEquals("${test_prefix}_101", unit.unitNumber)
        assertEquals(testProperty!!, unit.propertyId)
        assertEquals(testOrg!!, unit.orgId)
        assertEquals(2, unit.bedrooms)
        assertEquals(1, unit.bathrooms)
        assertEquals(750, unit.sqFt)
        assertEquals(1, unit.floor)
        assertEquals("Test unit", unit.notes)
    }

    @Test
    fun `getUnit should return created unit`() = runCoroutineTest {
        // Arrange
        val createResult = unitDatastore.createUnit(
            propertyId = testProperty!!,
            unitNumber = "${test_prefix}_102",
            bedrooms = 3,
            bathrooms = 2,
            sqFt = 1100,
            floor = 2,
            notes = null,
        ).registerUnitForDeletion()
        assertTrue(createResult.isSuccess)
        val created = createResult.getOrNull()!!

        // Act
        val getResult = unitDatastore.getUnit(created.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertEquals("${test_prefix}_102", fetched.unitNumber)
        assertEquals(3, fetched.bedrooms)
        assertEquals(2, fetched.bathrooms)
        assertEquals(1100, fetched.sqFt)
        assertEquals(2, fetched.floor)
        assertNull(fetched.notes)
    }

    @Test
    fun `getUnit should return null for non-existent unit`() = runCoroutineTest {
        // Arrange
        val fakeId = UnitId(UUID.random())

        // Act
        val result = unitDatastore.getUnit(fakeId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `getUnits should return all units for org`() = runCoroutineTest {
        // Arrange
        val result1 = unitDatastore.createUnit(
            propertyId = testProperty!!,
            unitNumber = "${test_prefix}_201",
            bedrooms = null,
            bathrooms = null,
            sqFt = null,
            floor = null,
            notes = null,
        ).registerUnitForDeletion()
        val result2 = unitDatastore.createUnit(
            propertyId = testProperty!!,
            unitNumber = "${test_prefix}_202",
            bedrooms = null,
            bathrooms = null,
            sqFt = null,
            floor = null,
            notes = null,
        ).registerUnitForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        // Act
        val listResult = unitDatastore.getUnits(orgId = testOrg!!)

        // Assert
        assertTrue(listResult.isSuccess)
        val units = listResult.getOrNull()
        assertNotNull(units)
        val unitNumbers = units.map { it.unitNumber }
        assertTrue(unitNumbers.contains("${test_prefix}_201"))
        assertTrue(unitNumbers.contains("${test_prefix}_202"))
    }

    @Test
    fun `getUnits should filter by propertyId`() = runCoroutineTest {
        // Arrange — create a second property with its own unit
        val otherProperty = createTestProperty("other-property-${test_prefix}", testUserId!!, testOrg!!)
        val result1 = unitDatastore.createUnit(
            propertyId = testProperty!!,
            unitNumber = "${test_prefix}_target",
            bedrooms = null,
            bathrooms = null,
            sqFt = null,
            floor = null,
            notes = null,
        ).registerUnitForDeletion()
        val result2 = unitDatastore.createUnit(
            propertyId = otherProperty,
            unitNumber = "${test_prefix}_other",
            bedrooms = null,
            bathrooms = null,
            sqFt = null,
            floor = null,
            notes = null,
        ).registerUnitForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        // Act
        val listResult = unitDatastore.getUnits(propertyId = testProperty!!)

        // Assert
        assertTrue(listResult.isSuccess)
        val unitNumbers = listResult.getOrNull()!!.map { it.unitNumber }
        assertTrue(unitNumbers.contains("${test_prefix}_target"))
        assertTrue(!unitNumbers.contains("${test_prefix}_other"))
    }

    @Test
    fun `updateUnit should update provided fields`() = runCoroutineTest {
        // Arrange
        val createResult = unitDatastore.createUnit(
            propertyId = testProperty!!,
            unitNumber = "${test_prefix}_301",
            bedrooms = 1,
            bathrooms = 1,
            sqFt = 500,
            floor = 3,
            notes = null,
        ).registerUnitForDeletion()
        assertTrue(createResult.isSuccess)
        val unit = createResult.getOrNull()!!

        // Act
        val updateResult = unitDatastore.updateUnit(
            unitId = unit.id,
            unitNumber = "${test_prefix}_301B",
            bedrooms = 2,
            bathrooms = null,
            sqFt = null,
            floor = null,
            notes = "Updated",
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertEquals("${test_prefix}_301B", updated.unitNumber)
        assertEquals(2, updated.bedrooms)
        assertEquals(1, updated.bathrooms) // unchanged
        assertEquals(500, updated.sqFt)    // unchanged
        assertEquals(3, updated.floor)     // unchanged
        assertEquals("Updated", updated.notes)
    }

    @Test
    fun `deleteUnit should soft-delete the unit`() = runCoroutineTest {
        // Arrange
        val createResult = unitDatastore.createUnit(
            propertyId = testProperty!!,
            unitNumber = "${test_prefix}_401",
            bedrooms = null,
            bathrooms = null,
            sqFt = null,
            floor = null,
            notes = null,
        )
        assertTrue(createResult.isSuccess)
        val unit = createResult.getOrNull()!!

        // Act
        val deleteResult = unitDatastore.deleteUnit(unit.id)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = unitDatastore.getUnit(unit.id)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())

        // Clean up (purge the soft-deleted record)
        unitDatastore.purgeUnit(unit.id)
    }

    @Test
    fun `deleteUnit should return false for non-existent unit`() = runCoroutineTest {
        // Arrange
        val fakeId = UnitId(UUID.random())

        // Act
        val deleteResult = unitDatastore.deleteUnit(fakeId)

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }
}
