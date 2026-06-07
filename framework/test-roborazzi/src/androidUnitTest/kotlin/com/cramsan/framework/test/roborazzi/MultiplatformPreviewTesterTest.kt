package com.cramsan.framework.test.roborazzi

import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

/**
 * Verifies the primary behaviour of [MultiplatformPreviewTester].
 *
 * [options] and [testParameters] are integration-boundary methods: their full execution requires
 * Robolectric and a composable classpath, so these tests verify the contracts that can be
 * observed without a live Android environment — option shape and parameter type invariants.
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
}
