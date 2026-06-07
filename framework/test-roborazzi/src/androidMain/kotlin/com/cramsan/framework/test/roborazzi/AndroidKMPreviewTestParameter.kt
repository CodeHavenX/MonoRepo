package com.cramsan.framework.test.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview
import sergio.sastre.composable.preview.scanner.jvm.JvmAnnotationInfo

/**
 * Test parameter for a single preview rendering.
 *
 * [variant] controls which Robolectric qualifiers are applied before rendering and which suffix
 * is appended to the screenshot filename. Defaults to [PreviewVariant.Default] (no overrides)
 * for bare `@Preview` functions.
 */
@OptIn(ExperimentalRoborazziApi::class)
data class AndroidKMPreviewTestParameter(
    override val composeTestRuleFactory: () -> AndroidComposeTestRule<ActivityScenarioRule<out ComponentActivity>, *>,
    override val preview: ComposablePreview<JvmAnnotationInfo>,
    val variant: PreviewVariant,
) : ComposePreviewTester.TestParameter.JUnit4TestParameter<JvmAnnotationInfo>(composeTestRuleFactory, preview) {

    /**
     * Used as the test name and as the Roborazzi `--tests` filter key.
     */
    override fun toString(): String {
        val suffix = if (variant.nameSuffix.isEmpty()) "" else "_${variant.nameSuffix}"
        return "${preview.declaringClass}:${preview.methodName}$suffix"
    }
}
