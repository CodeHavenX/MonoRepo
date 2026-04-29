package com.cramsan.framework.preferences.implementation

import com.cramsan.framework.preferences.PreferencesDelegateTest

/**
 * @Author cramsan
 * @created 1/16/2021
 */
class InMemoryPreferencesDelegateTest : PreferencesDelegateTest() {
    override fun createPreferencesDelegate() = InMemoryPreferencesDelegate()
}
