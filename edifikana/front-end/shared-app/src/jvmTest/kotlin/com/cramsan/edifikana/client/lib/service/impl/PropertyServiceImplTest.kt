package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.PropertyNetworkResponse
import com.cramsan.framework.preferences.Preferences
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [PropertyServiceImpl].
 * TODO: SKELETON FOR TESTING, NEEDS TO BE UPDATED AS CLASS IS NOT VERY TESTABLE ATM
 */
@Ignore
class PropertyServiceImplTest {
    private val httpClient = mockk<HttpClient>()
    private val preferences = mockk<Preferences>()
    private val service = PropertyServiceImpl(httpClient, preferences)

    /**
     * Tests that getPropertyList returns a mapped list of property models
     */
    @OptIn(NetworkModel::class)
    @Test
    fun `getPropertyList should return mapped property list and set active property`() = runTest {
        val propertyId = "property-1"
        val propertyList = listOf(
            mockk<PropertyNetworkResponse> {
//                coEvery { toPropertyModel() } returns testProperty1
            },
            mockk<PropertyNetworkResponse> {
//                coEvery { toPropertyModel() } returns testProperty2
            }
        )
        coEvery { preferences.loadString(any()) } returns propertyId
        mockkStatic("io.ktor.client.call.HttpClientCallKt")
        coEvery { httpClient.get(any<String>()) } returns mockk {
//            coEvery { body<List<PropertyNetworkResponse>>() } returns propertyList
        }

        val result = service.getPropertyList()
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Muralla", result.getOrNull()?.get(0)?.name)
        assertEquals("VicaVerde", result.getOrNull()?.get(1)?.name)
    }

    // Additional tests for setActiveProperty, getActiveProperty, etc. can be added as needed

    // Test properties for testing purposes
    val testProperty1 = PropertyModel(
        PropertyId("property-1"),
        "Muralla",
        "253 Jiron Juan Fanning, Barranco, Lima, Peru",
    )

    val testProperty2 = PropertyModel(
        PropertyId("property-2"),
        "VicaVerde",
        "632 Avenida Grau, Barranco, Lima, Peru"
    )
}

