package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.network.OrganizationNetworkListNetworkResponse
import com.cramsan.edifikana.lib.model.network.OrganizationNetworkResponse
import com.cramsan.edifikana.lib.serialization.createJson
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.assertlib.AssertUtil
import com.cramsan.framework.assertlib.implementation.NoopAssertUtil
import com.cramsan.framework.logging.EventLogger
import com.cramsan.framework.logging.implementation.PassthroughEventLogger
import com.cramsan.framework.logging.implementation.StdOutEventLoggerDelegate
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [OrganizationServiceImpl].
 */
class OrganizationServiceImplTest {
    private lateinit var ktorTestEngine: KtorTestEngine
    private lateinit var httpClient: HttpClient
    private lateinit var service: OrganizationServiceImpl
    private lateinit var json: Json

    @BeforeTest
    fun setupTest() {
        ktorTestEngine = KtorTestEngine()
        json = createJson()
        httpClient = HttpClient(ktorTestEngine.engine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        service = OrganizationServiceImpl(httpClient)

        AssertUtil.setInstance(NoopAssertUtil())
        EventLogger.setInstance(PassthroughEventLogger(StdOutEventLoggerDelegate()))
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `getOrganization should return mapped organization`() = runTest {
        // Arrange
        val networkResponse = OrganizationNetworkResponse(
            id = OrganizationId("org-1"),
            name = "Test Organization",
            description = "Test Description",
        )

        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getOrganization(OrganizationId("org-1"))

        // Assert
        assertTrue(result.isSuccess)
        val organization = result.getOrNull()
        assertEquals("org-1", organization?.id?.id)
        assertEquals("Test Organization", organization?.name)
        assertEquals("Test Description", organization?.description)
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `getOrganizations should return mapped organization list`() = runTest {
        // Arrange
        val networkResponse = OrganizationNetworkListNetworkResponse(
            listOf(
                OrganizationNetworkResponse(
                    id = OrganizationId("org-1"),
                    name = "Organization 1",
                    description = "Description 1",
                ),
                OrganizationNetworkResponse(
                    id = OrganizationId("org-2"),
                    name = "Organization 2",
                    description = "Description 2",
                ),
            )
        )

        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getOrganizations()

        // Assert
        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertEquals(2, list?.size)
        assertEquals("Organization 1", list?.get(0)?.name)
        assertEquals("Description 1", list?.get(0)?.description)
        assertEquals("Organization 2", list?.get(1)?.name)
        assertEquals("Description 2", list?.get(1)?.description)
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `getOrganizations should return empty list when no organizations exist`() = runTest {
        // Arrange
        val networkResponse = OrganizationNetworkListNetworkResponse(emptyList())

        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.getOrganizations()

        // Assert
        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertEquals(0, list?.size)
    }

    @OptIn(NetworkModel::class)
    @Test
    fun `createOrganization should return mapped organization after creation`() = runTest {
        // Arrange
        val networkResponse = OrganizationNetworkResponse(
            id = OrganizationId("org-new"),
            name = "New Organization",
            description = "New Description",
        )

        ktorTestEngine.configure {
            coEvery { produceResponse(any()) } returns MockResponseData.Success(
                json.encodeToString(networkResponse)
            )
        }

        // Act
        val result = service.createOrganization("New Organization", "New Description")

        // Assert
        assertTrue(result.isSuccess)
        val organization = result.getOrNull()
        assertEquals("org-new", organization?.id?.id)
        assertEquals("New Organization", organization?.name)
        assertEquals("New Description", organization?.description)
    }
}
