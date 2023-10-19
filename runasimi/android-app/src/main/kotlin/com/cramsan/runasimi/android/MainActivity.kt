package com.cramsan.runasimi.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cramsan.runasimi.mpplib.main.MainView
import com.cramsan.runasimi.mpplib.ui.theme.RunasimiTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point for the JB Compose Android sample app.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: AndroidMainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState by viewModel.uiModel.collectAsState()
            RunasimiTheme {
                MainView(
                    mainViewUIModel = uiState,
                    shuffleCards = { viewModel.changeStatement() },
                    playAudio = { viewModel.playAudio(it) },
                )
            }
        }

        viewModel.loadInitialData()
    }
}
