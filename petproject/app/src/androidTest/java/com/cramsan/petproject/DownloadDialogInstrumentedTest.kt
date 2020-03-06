package com.cramsan.petproject

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.cramsan.petproject.download.DownloadDialogActivity
import com.cramsan.petproject.mainmenu.MainMenuActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class DownloadDialogInstrumentedTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<DownloadDialogActivity>()

    @Test
    fun loadingScreenShows() {
        onView(withId(R.id.download_dialog_activity)).check(matches(isDisplayed()))
    }
}