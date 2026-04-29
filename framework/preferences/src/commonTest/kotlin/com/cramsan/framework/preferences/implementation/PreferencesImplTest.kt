package com.cramsan.framework.preferences.implementation

import com.cramsan.framework.preferences.PreferencesTest

class PreferencesImplTest : PreferencesTest() {
    override fun createPreferences() = InMemoryPreferencesDelegate()
}
