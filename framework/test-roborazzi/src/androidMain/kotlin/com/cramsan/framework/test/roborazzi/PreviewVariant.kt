package com.cramsan.framework.test.roborazzi

/**
 * Represents a single rendering permutation (colour scheme + form factor) for a preview test.
 *
 * [qualifier] is passed to [org.robolectric.RuntimeEnvironment.setQualifiers] before the
 * composable is rendered.
 * [nameSuffix] is appended to the screenshot filename so each variant gets a unique file.
 */
sealed class PreviewVariant(val qualifier: String, val nameSuffix: String) {
    /** No qualifier override — used for legacy bare `@Preview` functions. */
    data object Default : PreviewVariant(qualifier = "", nameSuffix = "")

    /** Phone form factor (~411 dp) — used by [DevicePreviews]. */
    data object Phone : PreviewVariant(qualifier = "+w411dp", nameSuffix = "Phone")

    /** Tablet form factor (~840 dp landscape) — used by [DevicePreviews]. */
    data object Tablet : PreviewVariant(qualifier = "+w840dp", nameSuffix = "Tablet")

    /** Desktop form factor (~1280 dp) — used by [DevicePreviews]. */
    data object Desktop : PreviewVariant(qualifier = "+w1280dp", nameSuffix = "Desktop")
}
