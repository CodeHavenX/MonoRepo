package com.cramsan.framework.preferences.implementation

import com.cramsan.framework.preferences.PreferencesDelegateTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Test
import java.util.prefs.Preferences
import kotlin.test.BeforeTest

/**
 * @Author cramsan
 * @created 1/16/2021
 */
@OptIn(ExperimentalCoroutinesApi::class)
class JVMPreferencesDelegateTest : PreferencesDelegateTest() {

    @BeforeTest
    fun setupTest() {
        // Clear any stored preferences
        val prefs: Preferences = Preferences.userNodeForPackage(JVMPreferencesDelegate::class.java)
        prefs.removeNode()

        preferencesDelegate = JVMPreferencesDelegate()
    }
}
