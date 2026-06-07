package com.cramsan.detektrules

import dev.detekt.test.lint
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoDirectPreviewAnnotationRuleTest {
    private fun rule() = NoDirectPreviewAnnotationRule(dev.detekt.test.TestConfig())

    // ── Should report ─────────────────────────────────────────────────────────

    @Test
    fun `composable function with direct @Preview - violation`() {
        val code =
            """
            annotation class Composable
            annotation class Preview
            @Preview
            @Composable
            fun MyScreenPreview() {}
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    @Test
    fun `composable function with @Preview in any order - violation`() {
        val code =
            """
            annotation class Composable
            annotation class Preview
            @Composable
            @Preview
            fun MyComponentPreview() {}
            """.trimIndent()
        assertEquals(1, rule().lint(code).size)
    }

    @Test
    fun `multiple composable functions with @Preview - one violation each`() {
        val code =
            """
            annotation class Composable
            annotation class Preview
            @Preview @Composable fun PreviewA() {}
            @Preview @Composable fun PreviewB() {}
            """.trimIndent()
        assertEquals(2, rule().lint(code).size)
    }

    // ── Should NOT report ─────────────────────────────────────────────────────

    @Test
    fun `composable function with @ScreenPreviews - no violation`() {
        val code =
            """
            annotation class Composable
            annotation class ScreenPreviews
            @ScreenPreviews
            @Composable
            fun MyScreenPreview() {}
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `composable function with @ComponentPreviews - no violation`() {
        val code =
            """
            annotation class Composable
            annotation class ComponentPreviews
            @ComponentPreviews
            @Composable
            fun MyComponentPreview() {}
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `non-composable function with @Preview - no violation`() {
        val code =
            """
            annotation class Preview
            @Preview
            fun plainFunction() {}
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `annotation class stacking @Preview - no violation`() {
        val code =
            """
            annotation class Composable
            annotation class Preview
            @Preview(name = "Light")
            @Preview(name = "Dark")
            annotation class ComponentPreviews
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }

    @Test
    fun `composable function with no annotations - no violation`() {
        val code =
            """
            annotation class Composable
            @Composable
            fun MyComposable() {}
            """.trimIndent()
        assertTrue(rule().lint(code).isEmpty())
    }
}
