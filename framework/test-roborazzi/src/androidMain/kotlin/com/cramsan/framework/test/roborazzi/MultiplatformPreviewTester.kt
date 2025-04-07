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
import com.github.takahirom.roborazzi.RoborazziActivity
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.provideRoborazziContext
import com.github.takahirom.roborazzi.registerRoborazziActivityToRobolectricIfNeeded
import org.junit.rules.RuleChain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.robolectric.Robolectric
import org.robolectric.util.Logger
import sergio.sastre.composable.preview.scanner.jvm.JvmAnnotationInfo
import sergio.sastre.composable.preview.scanner.jvm.JvmAnnotationScanner

/**
 * This tester class will be loaded by Roborazzi to generate the tests for the Compose Previews in Android and Common
 * source-sets.
 *
 * Sources:
 *  - https://github.com/CodeHavenX/MonoRepo/issues/113#issuecomment-2784480774
 *  - https://github.com/takahirom/roborazzi/tree/main/sample-generate-preview-tests-multiplatform
 */
@OptIn(ExperimentalRoborazziApi::class)
class MultiplatformPreviewTester : ComposePreviewTester<JUnit4TestParameter<JvmAnnotationInfo>> {
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

    override fun testParameters(): List<JUnit4TestParameter<JvmAnnotationInfo>> {
        val options = options()
        val annotations = listOf(
            "org.jetbrains.compose.ui.tooling.preview.Preview",
            "androidx.compose.ui.tooling.preview.Preview"
        )
        return annotations.map { JvmAnnotationScanner(it) }
            .map {
                it
                    .scanPackageTrees(*options.scanOptions.packages.toTypedArray())
                    .includePrivatePreviews()
                    .getPreviews()
            }
            .flatten()
            .map {
                AndroidKMPreviewTestParameter(
                    (
                        options.testLifecycleOptions as
                            ComposePreviewTester.Options.JUnit4TestLifecycleOptions
                        ).composeRuleFactory,
                    it
                )
            }
    }

    override fun test(testParameter: JUnit4TestParameter<JvmAnnotationInfo>) {
        // Setting the context to be able to access resources
        setupAndroidContextProvider()

        val preview = testParameter.preview
        testParameter.composeTestRule.setContent {
            preview()
        }

        val roborazziContext = provideRoborazziContext()
        val outputDir = roborazziContext.outputDirectory
        val filename = "${preview.declaringClass}_${preview.methodName}"
        val filepath = "$outputDir/$filename.${roborazziContext.imageExtension}"

        testParameter.composeTestRule.onRoot()
            .captureRoboImage(filepath)
    }

    // https://youtrack.jetbrains.com/issue/CMP-6612/Support-non-compose-UI-tests-with-resources
    // Configures Compose's AndroidContextProvider to access resources in tests.
    // See https://youtrack.jetbrains.com/issue/CMP-6612
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
            // Tests that don't depend on Compose will not have the provider class in classpath and will get
            // ClassNotFoundException. Skip configuring the provider for them.
            null
        }
    }
}
