package com.cramsan.edifikana.server.datastore.supabase

import com.cramsan.edifikana.lib.model.CommonAreaId
import com.cramsan.edifikana.lib.model.CommonAreaType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.utils.uuid.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabaseCommonAreaDatastoreIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var testPrefix: String
    private var propertyId: PropertyId? = null
    private var testUserId: UserId? = null
    private var orgId: OrganizationId? = null

    @BeforeTest
    fun setup() {
        testPrefix = UUID.random()
        runBlocking {
            testUserId = createTestUser("user-${testPrefix}@test.com")
            orgId = createTestOrganization("org_$testPrefix", "")
            propertyId = createTestProperty("${testPrefix}_Property", testUserId!!, orgId!!)
        }
    }

    @Test
    fun `createCommonArea should return created common area`() = runCoroutineTest {
        // Arrange

        // Act
        val result = commonAreaDatastore.createCommonArea(
            orgId = orgId!!,
            propertyId = propertyId!!,
            name = "${testPrefix}_Lobby",
            type = CommonAreaType.LOBBY,
            description = "Main entrance",
        ).registerCommonAreaForDeletion()

        // Assert
        assertTrue(result.isSuccess)
        val area = result.getOrNull()
        assertNotNull(area)
        assertEquals("${testPrefix}_Lobby", area.name)
        assertEquals(CommonAreaType.LOBBY, area.type)
        assertEquals("Main entrance", area.description)
        assertEquals(propertyId, area.propertyId)
        assertEquals(orgId, area.orgId)
    }

    @Test
    fun `getCommonArea should return created common area`() = runCoroutineTest {
        // Arrange
        val createResult = commonAreaDatastore.createCommonArea(
            orgId = orgId!!,
            propertyId = propertyId!!,
            name = "${testPrefix}_GetLobby",
            type = CommonAreaType.LOBBY,
            description = null,
        ).registerCommonAreaForDeletion()
        assertTrue(createResult.isSuccess)
        val created = createResult.getOrNull()!!

        // Act
        val getResult = commonAreaDatastore.getCommonArea(created.id)

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertEquals("${testPrefix}_GetLobby", fetched.name)
        assertEquals(created.id, fetched.id)
    }

    @Test
    fun `getCommonArea should return null when not found`() = runCoroutineTest {
        // Arrange
        val fakeId = CommonAreaId(UUID.random())

        // Act
        val result = commonAreaDatastore.getCommonArea(fakeId)

        // Assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `getCommonAreasForProperty should return all areas for property`() = runCoroutineTest {
        // Arrange

        // Act
        val result1 = commonAreaDatastore.createCommonArea(
            orgId = orgId!!,
            propertyId = propertyId!!,
            name = "${testPrefix}_Pool",
            type = CommonAreaType.POOL,
            description = null,
        ).registerCommonAreaForDeletion()
        val result2 = commonAreaDatastore.createCommonArea(
            orgId = orgId!!,
            propertyId = propertyId!!,
            name = "${testPrefix}_Gym",
            type = CommonAreaType.GYM,
            description = null,
        ).registerCommonAreaForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        val listResult = commonAreaDatastore.getCommonAreasForProperty(propertyId!!)

        // Assert
        assertTrue(listResult.isSuccess)
        val areas = listResult.getOrNull()
        assertNotNull(areas)
        val names = areas.map { it.name }
        assertTrue(names.contains("${testPrefix}_Pool"))
        assertTrue(names.contains("${testPrefix}_Gym"))
    }

    @Test
    fun `getCommonAreasForProperty should not return deleted areas`() = runCoroutineTest {
        // Arrange
        val createResult = commonAreaDatastore.createCommonArea(
            orgId = orgId!!,
            propertyId = propertyId!!,
            name = "${testPrefix}_Rooftop",
            type = CommonAreaType.ROOFTOP,
            description = null,
        ).registerCommonAreaForDeletion()
        assertTrue(createResult.isSuccess)
        val area = createResult.getOrNull()!!
        val deleteResult = commonAreaDatastore.deleteCommonArea(area.id)
        assertTrue(deleteResult.isSuccess)

        // Act
        val listResult = commonAreaDatastore.getCommonAreasForProperty(propertyId!!)

        // Assert
        assertTrue(listResult.isSuccess)
        val names = listResult.getOrNull()!!.map { it.name }
        assertTrue(!names.contains("${testPrefix}_Rooftop"))
    }

    @Test
    fun `updateCommonArea should update name and type`() = runCoroutineTest {
        // Arrange
        val createResult = commonAreaDatastore.createCommonArea(
            orgId = orgId!!,
            propertyId = propertyId!!,
            name = "${testPrefix}_OldName",
            type = CommonAreaType.LOBBY,
            description = "Original description",
        ).registerCommonAreaForDeletion()
        assertTrue(createResult.isSuccess)
        val area = createResult.getOrNull()!!

        // Act
        val updateResult = commonAreaDatastore.updateCommonArea(
            commonAreaId = area.id,
            name = "${testPrefix}_NewName",
            type = CommonAreaType.GYM,
            description = null,
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertEquals("${testPrefix}_NewName", updated.name)
        assertEquals(CommonAreaType.GYM, updated.type)
    }

    @Test
    fun `deleteCommonArea should soft delete and make area invisible`() = runCoroutineTest {
        // Arrange
        val createResult = commonAreaDatastore.createCommonArea(
            orgId = orgId!!,
            propertyId = propertyId!!,
            name = "${testPrefix}_ToDelete",
            type = CommonAreaType.PARKING,
            description = null,
        ).registerCommonAreaForDeletion()
        assertTrue(createResult.isSuccess)
        val area = createResult.getOrNull()!!

        // Act
        val deleteResult = commonAreaDatastore.deleteCommonArea(area.id)

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = commonAreaDatastore.getCommonArea(area.id)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `purgeCommonArea should hard delete the area`() = runCoroutineTest {
        // Arrange
        val createResult = commonAreaDatastore.createCommonArea(
            orgId = orgId!!,
            propertyId = propertyId!!,
            name = "${testPrefix}_ToPurge",
            type = CommonAreaType.LAUNDRY,
            description = null,
        )
        assertTrue(createResult.isSuccess)
        val area = createResult.getOrNull()!!

        // Act
        val purgeResult = commonAreaDatastore.purgeCommonArea(area.id)

        // Assert
        assertTrue(purgeResult.isSuccess)
        val getResult = commonAreaDatastore.getCommonArea(area.id)
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }
}
