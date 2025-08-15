package com.cramsan.framework.preferences.implementation

import com.cramsan.framework.preferences.PreferencesDelegateTest
import java.util.prefs.Preferences
import kotlin.test.BeforeTest

/**
 * @Author cramsan
 * @created 1/16/2021
 */
class JVMPreferencesDelegateTest : PreferencesDelegateTest() {

    @BeforeTest
    fun setupTest() {
        val nodeName = "com.cramsan.framework.preferences.implementation.JVMPreferencesDelegateTest"
        // Clear any stored preferences
        val prefs: Preferences = Preferences.userRoot().node(nodeName)
        prefs.removeNode()

        preferencesDelegate = JVMPreferencesDelegate(nodeName)
    }
}
