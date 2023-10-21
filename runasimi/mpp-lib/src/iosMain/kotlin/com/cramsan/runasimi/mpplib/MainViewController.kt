package com.cramsan.runasimi.mpplib

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.ComposeUIViewController
import com.cramsan.runasimi.mpplib.main.MainView
import com.cramsan.runasimi.mpplib.main.MainViewModel
import com.cramsan.runasimi.mpplib.ui.theme.RunasimiTheme

/**
 * ViewController to expose the screen for the iOS platform.
 */
@Suppress("FunctionNaming")
fun MainViewController(viewModel: MainViewModel) = ComposeUIViewController {

    val uiState by viewModel.uiModel.collectAsState()

    RunasimiTheme {
        MainView(
            mainViewUIModel = uiState,
            shuffleCards = { },
            playAudio = { },
        )
    }
}
