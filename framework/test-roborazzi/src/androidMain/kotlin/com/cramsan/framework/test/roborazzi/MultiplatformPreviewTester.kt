package com.cramsan.framework.test.roborazzi

import android.content.ContentProvider
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ComposePreviewTester.TestParameter.JUnit4TestParameter
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.InternalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziActivity
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.provideRoborazziContext
import com.github.takahirom.roborazzi.registerRoborazziActivityToRobolectricIfNeeded
import org.junit.rules.RuleChain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.util.Logger
import sergio.sastre.composable.preview.scanner.jvm.JvmAnnotationInfo
import sergio.sastre.composable.preview.scanner.jvm.JvmAnnotationScanner

/**
 * Roborazzi tester that renders Compose previews from Android and common source-sets.
 *
 * Three annotation classes are scanned:
 * - `@ScreenPreviews` — four variants: mobile/desktop × light/dark
 * - `@ComponentPreviews` — two variants: light/dark
 * - `@Preview` (legacy) — one variant with no qualifier overrides
 *
 * For each variant, [PreviewVariant.qualifier] is applied via [RuntimeEnvironment.setQualifiers]
 * before the composable is rendered, making `isSystemInDarkTheme()` and screen-width-based
 * layout logic behave correctly inside the preview.
 *
 * Sources:
 *  - https://github.com/CodeHavenX/MonoRepo/issues/113#issuecomment-2784480774
 *  - https://github.com/takahirom/roborazzi/tree/main/sample-generate-preview-tests-multiplatform
 */
@OptIn(ExperimentalRoborazziApi::class, InternalRoborazziApi::class)
class MultiplatformPreviewTester : ComposePreviewTester<JUnit4TestParameter<JvmAnnotationInfo>> {

    /**
     * Returns tester options configured with a [RoborazziActivity]-backed compose rule and the
     * variant-aware [TestWatcher] rule chain required by [test].
     */
    override fun options(): ComposePreviewTester.Options = super.options().copy(
        testLifecycleOptions = ComposePreviewTester.Options.JUnit4TestLifecycleOptions(
            composeRuleFactory = {
                @Suppress("UNCHECKED_CAST")
                createAndroidComposeRule<RoborazziActivity>()
                    as AndroidComposeTestRule<ActivityScenarioRule<out ComponentActivity>, *>
            },
            testRuleFactory = { composeTestRule ->
                RuleChain.outerRule(
                    object : TestWatcher() {
                        override fun starting(description: Description?) {
                            super.starting(description)
                            registerRoborazziActivityToRobolectricIfNeeded()
                        }
                    }
                )
                    .around(composeTestRule)
            }
        )
    )

    /**
     * Scans for [com.cramsan.ui.preview.DevicePreviews], [com.cramsan.ui.preview.ScreenPreviews],
     * [com.cramsan.ui.preview.ComponentPreviews], and legacy
     * [androidx.compose.ui.tooling.preview.Preview] functions and returns one
     * [AndroidKMPreviewTestParameter] per preview × variant combination.
     */
    override fun testParameters(): List<JUnit4TestParameter<JvmAnnotationInfo>> {
        val options = options()
        val composeRuleFactory = (
            options.testLifecycleOptions as
                ComposePreviewTester.Options.JUnit4TestLifecycleOptions
            ).composeRuleFactory

        val packages = options.scanOptions.packages.toTypedArray()

        val devicePreviews = JvmAnnotationScanner("com.cramsan.ui.preview.DevicePreviews")
            .scanPackageTrees(*packages)
            .includePrivatePreviews()
            .getPreviews()
            .flatMap { preview -> DEVICE_VARIANTS.map { variant -> preview to variant } }

        val singleVariantAnnotations = listOf(
            "com.cramsan.ui.preview.ScreenPreviews",
            "com.cramsan.ui.preview.ComponentPreviews",
        ) + LEGACY_PREVIEW_ANNOTATIONS

        val singleVariantPreviews = singleVariantAnnotations
            .map { JvmAnnotationScanner(it) }
            .flatMap { scanner ->
                scanner
                    .scanPackageTrees(*packages)
                    .includePrivatePreviews()
                    .getPreviews()
            }
            .map { preview -> preview to PreviewVariant.Default }

        return (devicePreviews + singleVariantPreviews).map { (preview, variant) ->
            AndroidKMPreviewTestParameter(composeRuleFactory, preview, variant)
        }
    }

    internal companion object {
        /** Variants applied to each `@DevicePreviews` function. */
        val DEVICE_VARIANTS = listOf(
            PreviewVariant.Phone,
            PreviewVariant.Tablet,
            PreviewVariant.Desktop,
        )

        /** Legacy `@Preview` annotation class names scanned for backwards compatibility. */
        val LEGACY_PREVIEW_ANNOTATIONS = listOf(
            "org.jetbrains.compose.ui.tooling.preview.Preview",
            "androidx.compose.ui.tooling.preview.Preview",
        )
    }

    /**
     * Renders a single preview variant and captures its screenshot.
     *
     * [testParameter] must be an [AndroidKMPreviewTestParameter]; every instance produced by
     * [testParameters] satisfies this requirement.  Passing any other [JUnit4TestParameter]
     * subtype will throw [IllegalArgumentException] with a descriptive message.
     */
    override fun test(testParameter: JUnit4TestParameter<JvmAnnotationInfo>) {
        setupAndroidContextProvider()

        val param = testParameter as? AndroidKMPreviewTestParameter
            ?: error(
                "test() requires AndroidKMPreviewTestParameter but received " +
                    "${testParameter::class.simpleName}. " +
                    "Ensure testParameters() is the sole source of test parameters.",
            )

        // Apply Robolectric qualifiers for this variant (e.g. night mode, screen width).
        if (param.variant.qualifier.isNotEmpty()) {
            RuntimeEnvironment.setQualifiers(param.variant.qualifier)
        }

        try {
            param.composeTestRule.setContent { param.preview() }

            val roborazziContext = provideRoborazziContext()
            val outputDir = roborazziContext.outputDirectory
            val suffix = if (param.variant.nameSuffix.isEmpty()) "" else "_${param.variant.nameSuffix}"
            val filename = "${param.preview.declaringClass}_${param.preview.methodName}$suffix"
            val filepath = "$outputDir/$filename.${roborazziContext.imageExtension}"

            param.composeTestRule.onRoot().captureRoboImage(filepath)
        } finally {
            // Reset qualifiers so state does not bleed into the next test.
            if (param.variant.qualifier.isNotEmpty()) {
                RuntimeEnvironment.setQualifiers("")
            }
        }
    }

    // https://youtrack.jetbrains.com/issue/CMP-6612/Support-non-compose-UI-tests-with-resources
    private fun setupAndroidContextProvider() {
        val type = findAndroidContextProvider() ?: return
        Robolectric.setupContentProvider(type)
    }

    private fun findAndroidContextProvider(): Class<ContentProvider>? {
        val providerClassName = "org.jetbrains.compose.resources.AndroidContextProvider"
        return try {
            @Suppress("UNCHECKED_CAST")
            Class.forName(providerClassName) as Class<ContentProvider>
        } catch (_: ClassNotFoundException) {
            Logger.debug("Class not found: $providerClassName")
            null
        }
    }
}
