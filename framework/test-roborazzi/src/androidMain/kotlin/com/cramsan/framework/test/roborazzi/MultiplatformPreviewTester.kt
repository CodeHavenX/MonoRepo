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
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
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
 * - `@DevicePreviews` — variants derived from its `@Preview` annotations (Phone, Tablet, Desktop)
 * - `@ScreenPreviews` — one variant derived from its single `@Preview` annotation
 * - `@ComponentPreviews` — one variant derived from its single `@Preview` annotation
 *
 * For each variant, the `widthDp` from the corresponding `@Preview` annotation is applied as a
 * Robolectric qualifier via [RuntimeEnvironment.setQualifiers], and the `name` is used as the
 * screenshot filename suffix. Both values are read at test-parameter-build time via ClassGraph so
 * that PreviewAnnotations.kt remains the single source of truth for dimensions.
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
     * and [com.cramsan.ui.preview.ComponentPreviews] functions and returns one
     * [AndroidKMPreviewTestParameter] per preview × variant combination.
     *
     * Variants (qualifier + name suffix) are derived from the `@Preview` annotations present on
     * each multipreview annotation class via a single ClassGraph scan, so
     * PreviewAnnotations.kt is the single source of truth.
     */
    override fun testParameters(): List<JUnit4TestParameter<JvmAnnotationInfo>> {
        val options = options()
        val composeRuleFactory = (
            options.testLifecycleOptions as
                ComposePreviewTester.Options.JUnit4TestLifecycleOptions
            ).composeRuleFactory

        val packages = options.scanOptions.packages.toTypedArray()

        val devicePreviewFunctions = JvmAnnotationScanner("com.cramsan.ui.preview.DevicePreviews")
            .scanPackageTrees(*packages)
            .includePrivatePreviews()
            .getPreviews()

        val devicePreviewKeys = devicePreviewFunctions
            .map { it.declaringClass to it.methodName }
            .toSet()

        val singleVariantAnnotations = listOf(
            "com.cramsan.ui.preview.ScreenPreviews",
            "com.cramsan.ui.preview.ComponentPreviews",
        )

        val allAnnotationClasses = listOf("com.cramsan.ui.preview.DevicePreviews") + singleVariantAnnotations
        val variantMap = ClassGraph()
            .enableAnnotationInfo()
            .acceptClasses(*allAnnotationClasses.toTypedArray())
            .scan()
            .use { scanResult -> allAnnotationClasses.associateWith { buildVariantsFrom(scanResult, it) } }

        val deviceVariants = variantMap.getValue("com.cramsan.ui.preview.DevicePreviews")
        val devicePreviews = devicePreviewFunctions
            .flatMap { preview -> deviceVariants.map { (qualifier, suffix) -> Triple(preview, qualifier, suffix) } }

        val singleVariantPreviews = singleVariantAnnotations
            .flatMap { annotationName ->
                val variants = variantMap.getValue(annotationName)
                JvmAnnotationScanner(annotationName)
                    .scanPackageTrees(*packages)
                    .includePrivatePreviews()
                    .getPreviews()
                    .filter { preview -> (preview.declaringClass to preview.methodName) !in devicePreviewKeys }
                    .flatMap { preview -> variants.map { (qualifier, suffix) -> Triple(preview, qualifier, suffix) } }
            }

        return (devicePreviews + singleVariantPreviews).map { (preview, qualifier, suffix) ->
            AndroidKMPreviewTestParameter(composeRuleFactory, preview, qualifier, suffix)
        }
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

        if (param.qualifier.isNotEmpty()) {
            RuntimeEnvironment.setQualifiers(param.qualifier)
        }

        try {
            param.composeTestRule.setContent { param.preview() }

            val roborazziContext = provideRoborazziContext()
            val outputDir = roborazziContext.outputDirectory
            val suffix = if (param.nameSuffix.isEmpty()) "" else "_${param.nameSuffix}"
            val filename = "${param.preview.methodName}${suffix}_${param.preview.declaringClass}"
            val filepath = "$outputDir/$filename.${roborazziContext.imageExtension}"

            param.composeTestRule.onRoot().captureRoboImage(filepath)
        } finally {
            if (param.qualifier.isNotEmpty()) {
                RuntimeEnvironment.setQualifiers("")
            }
        }
    }

    /**
     * Reads the `@Preview` annotations on [annotationClassName] via a single-class ClassGraph scan
     * and returns one `(qualifier, nameSuffix)` pair per annotation instance.
     *
     * Prefer calling [buildVariantsFrom] with a shared [ScanResult] inside [testParameters] to
     * avoid redundant classpath traversals. This overload is provided as a convenience entry point
     * for tests.
     */
    internal fun buildVariantsFor(annotationClassName: String): List<Pair<String, String>> =
        ClassGraph()
            .enableAnnotationInfo()
            .acceptClasses(annotationClassName)
            .scan()
            .use { buildVariantsFrom(it, annotationClassName) }

    /**
     * Extracts `(qualifier, nameSuffix)` pairs from the `@Preview` annotations present on
     * [annotationClassName] inside an already-open [scanResult].
     *
     * ClassGraph reads BINARY-retained annotations from class files and automatically unwraps
     * Kotlin repeatable-annotation containers, so multiple `@Preview` entries return as individual
     * items. Both the `org.jetbrains.compose` and `androidx.compose` annotation class names are
     * checked because the Compose Multiplatform compiler maps the former to the latter on Android.
     *
     * Returns `[("", "")]` — a single default variant — and logs a warning if the annotation class
     * is not on the classpath or carries no `@Preview` annotations.
     */
    private fun buildVariantsFrom(
        scanResult: ScanResult,
        annotationClassName: String,
    ): List<Pair<String, String>> {
        val classInfo = scanResult.getClassInfo(annotationClassName) ?: run {
            Logger.warn("$annotationClassName not found on classpath; using single default variant")
            return listOf("" to "")
        }
        val variants = PREVIEW_ANNOTATION_NAMES.flatMap { previewName ->
            classInfo.annotationInfo
                .filter { it.name == previewName }
                .map { annotation ->
                    val params = annotation.parameterValues
                    val widthDp = params.getValue("widthDp") as? Int ?: -1
                    val name = params.getValue("name") as? String ?: ""
                    val qualifier = if (widthDp > 0) "+w${widthDp}dp" else ""
                    qualifier to name
                }
        }
        if (variants.isEmpty()) {
            Logger.warn("No @Preview annotations found on $annotationClassName; using single default variant")
            return listOf("" to "")
        }
        return variants
    }

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

    companion object {
        // Both annotation class names are checked because the Compose Multiplatform compiler maps
        // org.jetbrains.compose.ui.tooling.preview.Preview to the androidx equivalent on Android.
        private val PREVIEW_ANNOTATION_NAMES = listOf(
            "org.jetbrains.compose.ui.tooling.preview.Preview",
            "androidx.compose.ui.tooling.preview.Preview",
        )
    }
}
