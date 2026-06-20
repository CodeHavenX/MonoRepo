package com.cramsan.framework.preferences.implementation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.cramsan.framework.preferences.PreferencesDelegateTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * @Author cramsan
 * @created 1/16/2021
 */

@RunWith(RobolectricTestRunner::class)
class AndroidPreferencesDelegateTest : PreferencesDelegateTest() {
    override fun createPreferencesDelegate(): PreferencesAndroid {
        val mockContext: Context = ApplicationProvider.getApplicationContext()
        return PreferencesAndroid(mockContext, "com.cramsan.framework.preferences.test")
    }
}
