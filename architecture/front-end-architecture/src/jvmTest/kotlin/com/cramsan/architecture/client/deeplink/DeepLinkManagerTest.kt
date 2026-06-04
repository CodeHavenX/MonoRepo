package com.cramsan.architecture.client.deeplink

import com.cramsan.framework.core.compose.navigation.Destination
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for [DeepLinkManager] and [DeepLinkParams].
 */
class DeepLinkManagerTest {

    @Serializable
    private data object TestDestination : Destination

    @Serializable
    private data object OtherDestination : Destination

    @Test
    fun `resolve returns null when no handlers registered`() {
        val router = DeepLinkManager()

        assertNull(router.resolve("edifikana://reset#type=recovery"))
    }

    @Test
    fun `resolve returns destination when handler matches`() {
        val router = DeepLinkManager()
        router.register { params ->
            if (params.params["type"] == "recovery") TestDestination else null
        }

        assertEquals(TestDestination, router.resolve("edifikana://reset#type=recovery"))
    }

    @Test
    fun `resolve returns null when handler does not match`() {
        val router = DeepLinkManager()
        router.register { params ->
            if (params.params["type"] == "recovery") TestDestination else null
        }

        assertNull(router.resolve("edifikana://reset#type=other"))
    }

    @Test
    fun `resolve uses first matching handler when multiple are registered`() {
        val router = DeepLinkManager()
        router.register { params ->
            if (params.params["type"] == "recovery") TestDestination else null
        }
        router.register { OtherDestination }

        assertEquals(TestDestination, router.resolve("edifikana://reset#type=recovery"))
    }

    @Test
    fun `resolve falls through to second handler when first returns null`() {
        val router = DeepLinkManager()
        router.register { null }
        router.register { OtherDestination }

        assertEquals(OtherDestination, router.resolve("some://link"))
    }

    @Test
    fun `parse extracts params from fragment URL`() {
        val params = DeepLinkParams.parse("edifikana://reset#access_token=abc&type=recovery")

        assertEquals("abc", params.params["access_token"])
        assertEquals("recovery", params.params["type"])
    }

    @Test
    fun `parse extracts params from query URL`() {
        val params = DeepLinkParams.parse("http://localhost:8080/?code=xyz&type=recovery")

        assertEquals("xyz", params.params["code"])
        assertEquals("recovery", params.params["type"])
    }

    @Test
    fun `parse handles bare key-value string`() {
        val params = DeepLinkParams.parse("type=recovery&code=abc")

        assertEquals("recovery", params.params["type"])
        assertEquals("abc", params.params["code"])
    }

    @Test
    fun `parse returns empty params for empty input`() {
        val params = DeepLinkParams.parse("")

        assertEquals(emptyMap(), params.params)
    }

    @Test
    fun `parse preserves rawInput`() {
        val input = "edifikana://reset#type=recovery"

        assertEquals(input, DeepLinkParams.parse(input).rawInput)
    }
}
