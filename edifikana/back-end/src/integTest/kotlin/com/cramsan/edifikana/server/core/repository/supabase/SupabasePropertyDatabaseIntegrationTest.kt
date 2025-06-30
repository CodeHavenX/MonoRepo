package com.cramsan.edifikana.server.core.repository.supabase

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.edifikana.server.core.service.models.Property
import com.cramsan.edifikana.server.core.service.models.requests.CreatePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.DeletePropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyListsRequest
import com.cramsan.edifikana.server.core.service.models.requests.GetPropertyRequest
import com.cramsan.edifikana.server.core.service.models.requests.UpdatePropertyRequest
import com.cramsan.framework.utils.uuid.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SupabasePropertyDatabaseIntegrationTest : SupabaseIntegrationTest() {

    private lateinit var test_prefix: String
    private var testUserId: UserId? = null

    @BeforeTest
    fun setup() {
        test_prefix = UUID.random()
        testUserId = UserId("user-${test_prefix}test_prefix")
    }

    @Test
    fun `createProperty should return property on success`() = runCoroutineTest {
        // Arrange
        val request = CreatePropertyRequest(
            name = "${test_prefix}_Property",
        )
        // Act
        val result = propertyDatabase.createProperty(request).registerPropertyForDeletion()
        // Assert
        assertTrue(result.isSuccess)
        assertEquals(
            Property(
                id = result.getOrNull()!!.id,
                name = request.name,
            ),
            result.getOrNull(),
        )

        // Clean up
        propertyDatabase.deleteProperty(DeletePropertyRequest(result.getOrNull()!!.id)).getOrThrow()
    }

    @Test
    fun `getProperty should return created property`() = runCoroutineTest {
        // Arrange
        val createRequest = CreatePropertyRequest(
            name = "${test_prefix}_GetProperty",
        )

        // Act
        val createResult = propertyDatabase.createProperty(createRequest).registerPropertyForDeletion()
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!
        val getResult = propertyDatabase.getProperty(GetPropertyRequest(property.id))

        // Assert
        assertTrue(getResult.isSuccess)
        val fetched = getResult.getOrNull()
        assertNotNull(fetched)
        assertTrue(fetched.name == createRequest.name)

        // Clean up
    }

    @Test
    fun `getProperties should return all properties for user`() = runCoroutineTest {
        // Arrange
        val request1 = CreatePropertyRequest(
            name = "${test_prefix}_PropertyA",
        )
        val request2 = CreatePropertyRequest(
            name = "${test_prefix}_PropertyB",
        )

        // Act
        val result1 = propertyDatabase.createProperty(request1).registerPropertyForDeletion()
        val result2 = propertyDatabase.createProperty(request2).registerPropertyForDeletion()
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        val getAllResult = propertyDatabase.getProperties(GetPropertyListsRequest(userId = testUserId!!, showAll = true))

        // Assert
        assertTrue(getAllResult.isSuccess)
        val properties = getAllResult.getOrNull()
        assertNotNull(properties)
        val names = properties!!.map { it.name }
        assertTrue(names.contains(request1.name))
        assertTrue(names.contains(request2.name))
    }

    @Test
    fun `updateProperty should update property fields`() = runCoroutineTest {
        // Arrange
        val createRequest = CreatePropertyRequest(
            name = "${test_prefix}_ToUpdate",
        )
        val createResult = propertyDatabase.createProperty(createRequest)
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!
        val updateRequest = UpdatePropertyRequest(
            propertyId = property.id,
            name = "${test_prefix}_UpdatedName",
        )

        // Act
        val updateResult = propertyDatabase.updateProperty(updateRequest).registerPropertyForDeletion()

        // Assert
        assertTrue(updateResult.isSuccess)
        val updated = updateResult.getOrNull()
        assertNotNull(updated)
        assertTrue(updated.name == updateRequest.name)
    }

    @Test
    fun `deleteProperty should remove property`() = runCoroutineTest {
        // Arrange
        val createRequest = CreatePropertyRequest(
            name = "${test_prefix}_ToDelete",
        )
        val createResult = propertyDatabase.createProperty(createRequest)
        assertTrue(createResult.isSuccess)
        val property = createResult.getOrNull()!!

        // Act
        val deleteResult = propertyDatabase.deleteProperty(DeletePropertyRequest(property.id))

        // Assert
        assertTrue(deleteResult.isSuccess)
        assertTrue(deleteResult.getOrNull() == true)
        val getResult = propertyDatabase.getProperty(GetPropertyRequest(property.id))
        assertTrue(getResult.isSuccess)
        assertNull(getResult.getOrNull())
    }

    @Test
    fun `deleteProperty should fail for non-existent property`() = runCoroutineTest {
        // Arrange
        val fakeId = PropertyId("fake-${test_prefix}")

        // Act
        val deleteResult = propertyDatabase.deleteProperty(DeletePropertyRequest(fakeId))

        // Assert
        assertTrue(deleteResult.isFailure || deleteResult.getOrNull() == false)
    }
}
