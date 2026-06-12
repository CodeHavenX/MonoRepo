package com.cramsan.ui.preview

import org.jetbrains.compose.ui.tooling.preview.Preview

// The common @Preview annotation does not expose Android-only parameters such as `uiMode`,
// `device`, or `widthDp`.  Dark/light switching and form-factor sizing for Roborazzi screenshots
// are applied by MultiplatformPreviewTester via RuntimeEnvironment.setQualifiers; the annotation
// name ("Light", "Dark", "Mobile - Light", …) is the only signal the tester reads.

/**
 * Multipreview annotation for individual UI components.
 *
 * Prefer this over `@Preview` directly so that the screenshot suite are more configurable
 */
@Preview
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class ComponentPreviews

/**
 * Multipreview annotation for full-screen composables.
 *
 * Prefer this over `@Preview` directly so that the screenshot suite can be more configurable
 */
@Preview
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class ScreenPreviews

/**
 * Multipreview annotation generating three preview variants (no light/dark distinction):
 * - Phone (~411 dp wide)
 * - Tablet (~840 dp wide, landscape)
 * - Desktop (~1280 dp wide)
 *
 * Use this when the focus is adaptive layout across form factors rather than colour-scheme
 * coverage.
 */
@Preview(name = "Phone", widthDp = 411)
@Preview(name = "Tablet", widthDp = 840)
@Preview(name = "Desktop", widthDp = 1280)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class DevicePreviews
