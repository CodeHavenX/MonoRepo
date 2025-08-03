package com.cramsan.sample.compose.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cramsan.sample.mpplib.compose.common.MainView

/**
 * Main entry point for the JB Compose Android sample app.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainView()
        }
    }
}

@Preview
@Composable
private fun MainActivityPreview() {
    MainView()
}
