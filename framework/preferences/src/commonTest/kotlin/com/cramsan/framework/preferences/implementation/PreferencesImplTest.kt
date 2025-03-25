package com.cramsan.framework.preferences.implementation

import com.cramsan.framework.preferences.PreferencesTest
import kotlin.test.BeforeTest
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * @Author cramsan
 * @created 1/16/2021
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesImplTest : PreferencesTest() {

    @BeforeTest
    fun setupTest() {
        val preferencesDelegate = InMemoryPreferencesDelegate()
        preferences = PreferencesImpl(preferencesDelegate)
    }
}
