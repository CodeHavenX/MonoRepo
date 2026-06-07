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
 * [qualifier] is passed to [org.robolectric.RuntimeEnvironment.setQualifiers] before rendering.
 * [nameSuffix] is appended to the screenshot filename so each variant gets a unique file.
 * Both values are derived from the [org.jetbrains.compose.ui.tooling.preview.Preview] annotation
 * on the enclosing multipreview annotation class.
 */
@OptIn(ExperimentalRoborazziApi::class)
data class AndroidKMPreviewTestParameter(
    override val composeTestRuleFactory: () -> AndroidComposeTestRule<ActivityScenarioRule<out ComponentActivity>, *>,
    override val preview: ComposablePreview<JvmAnnotationInfo>,
    val qualifier: String,
    val nameSuffix: String,
) : ComposePreviewTester.TestParameter.JUnit4TestParameter<JvmAnnotationInfo>(composeTestRuleFactory, preview) {

    override fun toString(): String {
        val suffix = if (nameSuffix.isEmpty()) "" else "_$nameSuffix"
        return "${preview.declaringClass}:${preview.methodName}$suffix"
    }
}
