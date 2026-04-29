package com.cramsan.framework.preferences.implementation

import com.cramsan.framework.preferences.PreferencesDelegateTest
import java.util.prefs.Preferences

/**
 * @Author cramsan
 * @created 1/16/2021
 */
class JVMPreferencesDelegateTest : PreferencesDelegateTest() {
    override fun createPreferencesDelegate(): JVMPreferencesDelegate {
        val nodeName = "com.cramsan.framework.preferences.implementation.JVMPreferencesDelegateTest"
        Preferences.userRoot().node(nodeName).removeNode()
        return JVMPreferencesDelegate(nodeName)
    }
}
