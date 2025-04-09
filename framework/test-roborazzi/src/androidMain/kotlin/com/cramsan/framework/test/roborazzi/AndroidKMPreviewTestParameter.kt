package com.cramsan.framework.test.roborazzi

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.ComposePreviewTester
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview
import sergio.sastre.composable.preview.scanner.jvm.JvmAnnotationInfo

/**
 * This is a parameter to generate a test for the Android Compose Test Rule.
 */
@OptIn(ExperimentalRoborazziApi::class)
data class AndroidKMPreviewTestParameter(
    override val composeTestRuleFactory: () -> AndroidComposeTestRule<ActivityScenarioRule<out ComponentActivity>, *>,
    override val preview: ComposablePreview<JvmAnnotationInfo>,
) : ComposePreviewTester.TestParameter.JUnit4TestParameter<JvmAnnotationInfo>(composeTestRuleFactory, preview) {

    /**
     * This is used as a mechanism to set the name of the test.
     */
    override fun toString(): String {
        return "${preview.declaringClass}:${preview.methodName}"
    }
}
