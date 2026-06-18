package com.cramsan.framework.test.roborazzi

import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Verifies the primary behaviour of [MultiplatformPreviewTester].
 *
 * [options] and [testParameters] are integration-boundary methods: their full execution requires
 * Robolectric and a composable classpath, so these tests verify the contracts that can be
 * observed without a live Android environment — option shape and parameter type invariants.
 *
 * [buildVariantsFor] is tested via its `internal` visibility using the real annotation classes
 * from the `:framework:ui-preview` module, which is a test dependency of this module.
 */
@OptIn(ExperimentalRoborazziApi::class)
class MultiplatformPreviewTesterTest {

    private val tester = MultiplatformPreviewTester()

    @Test
    fun `options returns JUnit4 lifecycle options`() {
        val options = tester.options()
        assertIs<ComposePreviewTester.Options.JUnit4TestLifecycleOptions>(options.testLifecycleOptions)
    }

    @Test
    fun `testParameters fails fast when no packages are configured`() {
        // The default options() returns empty scanOptions.packages; JvmAnnotationScanner
        // enforces a non-empty package list so callers configure it via @RoborazziConfig.
        assertFailsWith<IllegalArgumentException> {
            tester.testParameters()
        }
    }

    @Test
    fun `buildVariantsFor DevicePreviews returns Phone Tablet Desktop variants`() {
        val variants = tester.buildVariantsFor("com.cramsan.ui.preview.DevicePreviews")

        assertEquals(3, variants.size)
        assertTrue(variants.any { (qualifier, name) -> qualifier == "+w411dp" && name == "Phone" })
        assertTrue(variants.any { (qualifier, name) -> qualifier == "+w840dp" && name == "Tablet" })
        assertTrue(variants.any { (qualifier, name) -> qualifier == "+w1280dp" && name == "Desktop" })
    }

    @Test
    fun `buildVariantsFor ScreenPreviews returns single default variant`() {
        val variants = tester.buildVariantsFor("com.cramsan.ui.preview.ScreenPreviews")

        assertEquals(1, variants.size)
        assertEquals("" to "", variants.single())
    }

    @Test
    fun `buildVariantsFor unknown class returns single default variant`() {
        val variants = tester.buildVariantsFor("com.cramsan.does.not.exist.FakeAnnotation")

        assertEquals(1, variants.size)
        assertEquals("" to "", variants.single())
    }
}
