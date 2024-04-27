package com.cramsan.runasimi.android

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.runasimi.mpplib.main.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AndroidMainViewModel @Inject constructor(
    private val viewModel: MainViewModel,
    application: Application,
) : AndroidViewModel(application) {

    val uiModel = viewModel.uiModel

    fun loadInitialData() {
        viewModelScope.launch {
            viewModel.loadInitialData()
        }
    }
    fun changeStatement() {
        viewModelScope.launch {
            viewModel.setStatementSeed()
        }
    }

    fun playAudio(selectedIndex: Int) {
        viewModelScope.launch {
            viewModel.playAudioStatement(selectedIndex)
        }
    }
}
