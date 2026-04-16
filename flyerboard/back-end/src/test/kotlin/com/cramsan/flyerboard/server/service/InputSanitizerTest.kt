package com.cramsan.flyerboard.server.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [InputSanitizer].
 */
class InputSanitizerTest {

    @Test
    fun `sanitizeText strips HTML tags but keeps text content`() {
        // The regex removes angle-bracket tags; text between tags is preserved
        val result = InputSanitizer.sanitizeText("<script>alert('xss')</script>Title", maxLength = 200)
        assertEquals("alert('xss')Title", result)
    }

    @Test
    fun `sanitizeText strips nested HTML tags`() {
        val result = InputSanitizer.sanitizeText("<b><i>Bold italic</i></b>", maxLength = 200)
        assertEquals("Bold italic", result)
    }

    @Test
    fun `sanitizeText trims leading and trailing whitespace`() {
        val result = InputSanitizer.sanitizeText("  hello world  ", maxLength = 200)
        assertEquals("hello world", result)
    }

    @Test
    fun `sanitizeText truncates to maxLength`() {
        val input = "a".repeat(300)
        val result = InputSanitizer.sanitizeText(input, maxLength = 200)
        assertEquals(200, result.length)
    }

    @Test
    fun `sanitizeText returns empty string for blank input`() {
        val result = InputSanitizer.sanitizeText("   ", maxLength = 200)
        assertEquals("", result)
    }

    @Test
    fun `sanitizeText returns empty string for empty input`() {
        val result = InputSanitizer.sanitizeText("", maxLength = 200)
        assertEquals("", result)
    }

    @Test
    fun `sanitizeText passes clean text through unchanged`() {
        val clean = "This is a clean title"
        val result = InputSanitizer.sanitizeText(clean, maxLength = 200)
        assertEquals(clean, result)
    }

    @Test
    fun `sanitizeText strips HTML and then truncates`() {
        // After stripping "Hello" becomes 5 chars; maxLength = 3 should truncate to "Hel"
        val result = InputSanitizer.sanitizeText("<b>Hello</b>", maxLength = 3)
        assertEquals("Hel", result)
    }

    @Test
    fun `sanitizeText strips HTML tags from description`() {
        val result = InputSanitizer.sanitizeText("<p>Some <em>formatted</em> text.</p>", maxLength = 2000)
        assertEquals("Some formatted text.", result)
    }
}
