package com.cramsan.framework.preferences.implementation

import com.cramsan.framework.preferences.PreferencesDelegateTest
import kotlin.test.BeforeTest

/**
 * @Author cramsan
 * @created 1/16/2021
 */
class InMemoryPreferencesDelegateTest : PreferencesDelegateTest() {

    @BeforeTest
    fun setupTest() {
        preferencesDelegate = InMemoryPreferencesDelegate()
    }
}
