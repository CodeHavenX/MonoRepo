package com.cramsan.framework.sample.shared

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cramsan.framework.sample.shared.features.ApplicationScreen

/**
 * Main activity for the application.
 */
class MainActivity : ComponentActivity() {

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ApplicationScreen()
        }
    }
}
