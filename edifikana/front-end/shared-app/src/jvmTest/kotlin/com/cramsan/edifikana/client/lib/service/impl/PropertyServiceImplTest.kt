package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.PropertyListNetworkResponse
import com.cramsan.edifikana.lib.model.network.PropertyNetworkResponse
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import com.cramsan.framework.preferences.Preferences
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [PropertyServiceImpl].
 */
class PropertyServiceImplTest {
    private lateinit var ktorTestEngine: KtorTestEngine
    private lateinit var httpClient: HttpClient
    private lateinit var service: PropertyServiceImpl
    private lateinit var json: Json
    private lateinit var preferences: Preferences

    @BeforeTest
    fun setupTest() {
        ktorTestEngine = KtorTestEngine()
        json = createJson()
        httpClient = HttpClient(ktorTestEngine.engine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        preferences = mockk(relaxed = true)
        service = PropertyServiceImpl(httpClient, preferences)

        AssertUtil.setInstance(NoopAssertUtil())
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `getPropertyList should return mapped property list and set active from preferences`() = runTest {
        // Arrange
        val networkResponse = PropertyListNetworkResponse(listOf(
            PropertyNetworkResponse(
                id = PropertyId("property-1"),
                name = "Prop 1",
                address = "Address 1",
                organizationId = OrganizationId("org-1"),
            ),
            PropertyNetworkResponse(
                id = PropertyId("property-2"),
                name = "Prop 2",
                address = "Address 2",
                organizationId = OrganizationId("org-1"),
            ),
        ))

        coEvery { preferences.loadString(PropertyServiceImpl.PREF_ACTIVE_PROPERTY) } returns "property-2"

        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getPropertyList()

        // Assert
        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertEquals(2, list?.size)
        assertEquals("Prop 1", list?.get(0)?.name)
        // active property should be set from preferences
        assertEquals(PropertyId("property-2"), service.activeProperty().value)
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `getPropertyList should select first when preference not found`() = runTest {
        // Arrange
        val networkResponse = PropertyListNetworkResponse(listOf(
            PropertyNetworkResponse(
                id = PropertyId("property-10"),
                name = "FirstProp",
                address = "A1",
                organizationId = OrganizationId("org-1"),
            ),
            PropertyNetworkResponse(
                id = PropertyId("property-20"),
                name = "SecondProp",
                address = "A2",
                organizationId = OrganizationId("org-1"),
            ),
        ))

        // preferences returns id that does not match any property
        coEvery { preferences.loadString(PropertyServiceImpl.PREF_ACTIVE_PROPERTY) } returns "not-found"

        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getPropertyList()

        // Assert
        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertEquals(2, list?.size)
        assertEquals(PropertyId("property-10"), service.activeProperty().value)
    }

    @Test
    fun `activeProperty should be null by default`() {
        assertEquals(null, service.activeProperty().value)
    }

    @Test
    fun `setActiveProperty should save to preferences and update flow`() {
        // Arrange
        val id = PropertyId("set-1")
        coEvery { preferences.saveString(PropertyServiceImpl.PREF_ACTIVE_PROPERTY, id.propertyId) } returns Unit

        // Act
        val result = service.setActiveProperty(id)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { preferences.saveString(PropertyServiceImpl.PREF_ACTIVE_PROPERTY, id.propertyId) }
        assertEquals(id, service.activeProperty().value)
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `getProperty should return mapped property`() = runTest {
        // Arrange
        val networkResponse = PropertyNetworkResponse(
            id = PropertyId("p-get"),
            name = "GetProp",
            address = "Addr",
            organizationId = OrganizationId("org-1"),
        )

        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getProperty(PropertyId("p-get"))

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("GetProp", result.getOrNull()?.name)
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `addProperty should return mapped property after creation`() = runTest {
        // Arrange
        val networkResponse = PropertyNetworkResponse(
            id = PropertyId("p-new"),
            name = "NewProp",
            address = "NewAddr",
            organizationId = OrganizationId("org-1"),
        )

        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.addProperty("NewProp", "NewAddr", OrganizationId("org-1"))

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("NewProp", result.getOrNull()?.name)
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `updateProperty should return mapped property after update`() = runTest {
        // Arrange
        val networkResponse = PropertyNetworkResponse(
            id = PropertyId("p-upd"),
            name = "Updated",
            address = "UpdAddr",
            organizationId = OrganizationId("org-1"),
        )

        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.updateProperty(PropertyId("p-upd"), "Updated", "UpdAddr")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Updated", result.getOrNull()?.name)
    }

    @Test
    fun `removeProperty should return failure as not implemented`() = runTest {
        // Act
        val result = service.removeProperty(PropertyId("any"))

        // Assert
        assertTrue(result.isFailure)
    }
}

