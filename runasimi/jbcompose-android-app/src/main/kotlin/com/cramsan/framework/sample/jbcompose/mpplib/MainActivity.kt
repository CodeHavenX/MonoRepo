package com.cramsan.framework.sample.jbcompose.mpplib

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.cramsan.minesweepers.common.main.MainView
import com.cramsan.minesweepers.common.main.MainViewModel
import com.cramsan.minesweepers.common.ui.theme.RunasimiTheme
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
                    cardUiModels = uiState,
                ) {
                    viewModel.changeStatement()
                }
            }
        }

        viewModel.loadInitialData()
    }
}
