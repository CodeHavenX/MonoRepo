package com.cramsan.edifikana.client.lib.features.home.shared

import com.cramsan.edifikana.client.ui.components.ImageSource
import com.cramsan.edifikana.client.ui.resources.PropertyIcons
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for PropertyIconOptions.
 */
class PropertyIconOptionsTest {

    @Test
    fun `getDefaultOptions returns all available icons`() {
        // Act
        val options = PropertyIconOptions.getDefaultOptions()

        // Assert
        assertEquals(5, options.size)
        assertEquals("CASA", options[0].id)
        assertEquals("Casa", options[0].displayName)
        assertEquals("QUINTA", options[1].id)
        assertEquals("Quinta", options[1].displayName)
        assertEquals("L_DEPA", options[2].id)
        assertEquals("Large Department", options[2].displayName)
        assertEquals("M_DEPA", options[3].id)
        assertEquals("Medium Department", options[3].displayName)
        assertEquals("S_DEPA", options[4].id)
        assertEquals("Small Department", options[4].displayName)
    }

    @Test
    fun `toImageUrl converts drawable option to correct string format`() {
        // Arrange
        val options = PropertyIconOptions.getDefaultOptions()
        val casaOption = options.first { it.id == "CASA" }

        // Act
        val result = PropertyIconOptions.toImageUrl(casaOption)

        // Assert
        assertEquals("drawable:CASA", result)
    }

    @Test
    fun `toImageUrl returns null for null option`() {
        // Act
        val result = PropertyIconOptions.toImageUrl(null)

        // Assert
        assertNull(result)
    }

    @Test
    fun `fromImageUrl converts drawable string to correct option`() {
        // Act
        val result = PropertyIconOptions.fromImageUrl("drawable:QUINTA")

        // Assert
        assertNotNull(result)
        assertEquals("QUINTA", result.id)
        assertEquals("Quinta", result.displayName)
        assert(result.imageSource is ImageSource.Drawable)
    }

    @Test
    fun `fromImageUrl converts all drawable strings correctly`() {
        // Test all icons
        val iconIds = listOf("CASA", "QUINTA", "L_DEPA", "M_DEPA", "S_DEPA")

        iconIds.forEach { iconId ->
            // Act
            val result = PropertyIconOptions.fromImageUrl("drawable:$iconId")

            // Assert
            assertNotNull(result, "Expected result for $iconId")
            assertEquals(iconId, result.id)
            assert(result.imageSource is ImageSource.Drawable)
        }
    }

    @Test
    fun `fromImageUrl handles custom URL`() {
        // Arrange
        val customUrl = "https://example.com/property-image.png"

        // Act
        val result = PropertyIconOptions.fromImageUrl(customUrl)

        // Assert
        assertNotNull(result)
        assertEquals("custom_url", result.id)
        assertEquals("Custom Image", result.displayName)
        assert(result.imageSource is ImageSource.Url)
        assertEquals(customUrl, (result.imageSource as ImageSource.Url).url)
    }

    @Test
    fun `fromImageUrl returns null for null string`() {
        // Act
        val result = PropertyIconOptions.fromImageUrl(null)

        // Assert
        assertNull(result)
    }

    @Test
    fun `fromImageUrl returns null for invalid drawable ID`() {
        // Act
        val result = PropertyIconOptions.fromImageUrl("drawable:INVALID_ID")

        // Assert
        assertNull(result)
    }

    @Test
    fun `fromImageUrl returns null for invalid format`() {
        // Act
        val result = PropertyIconOptions.fromImageUrl("invalid-format")

        // Assert
        assertNull(result)
    }

    @Test
    fun `round trip conversion preserves drawable icon`() {
        // Arrange
        val options = PropertyIconOptions.getDefaultOptions()
        val originalOption = options.first { it.id == "M_DEPA" }

        // Act
        val imageUrl = PropertyIconOptions.toImageUrl(originalOption)
        val convertedOption = PropertyIconOptions.fromImageUrl(imageUrl)

        // Assert
        assertNotNull(convertedOption)
        assertEquals(originalOption.id, convertedOption.id)
        assertEquals(originalOption.displayName, convertedOption.displayName)
    }

    @Test
    fun `round trip conversion preserves custom URL`() {
        // Arrange
        val customUrl = "https://example.com/my-property.jpg"
        val originalOption = PropertyIconOptions.fromImageUrl(customUrl)

        // Act
        val imageUrl = PropertyIconOptions.toImageUrl(originalOption)
        val convertedOption = PropertyIconOptions.fromImageUrl(imageUrl)

        // Assert
        assertNotNull(convertedOption)
        assertEquals(customUrl, imageUrl)
        assertEquals("custom_url", convertedOption.id)
        assert(convertedOption.imageSource is ImageSource.Url)
        assertEquals(customUrl, (convertedOption.imageSource as ImageSource.Url).url)
    }
}
