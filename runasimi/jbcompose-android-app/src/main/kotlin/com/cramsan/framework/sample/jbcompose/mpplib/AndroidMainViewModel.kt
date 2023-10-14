package com.cramsan.framework.sample.jbcompose.mpplib

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.minesweepers.common.main.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AndroidMainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val viewModel: MainViewModel,
) : ViewModel() {

    val uiModel = viewModel.uiModel

    fun loadInitialData() {
        val startingSeed = savedStateHandle.get<Int>("seed").let {
            if (it == null) {
                val newSeed = Random.nextInt()
                savedStateHandle["seed"] = newSeed
                newSeed
            } else {
                it
            }
        }
        loadSavedSeed(startingSeed)
    }
    fun changeStatement() {
        savedSeed(Random.nextInt())
    }

    private fun loadSavedSeed(fallbackSeed: Int) {
        val newSeed = savedStateHandle["seed"] ?: fallbackSeed
        viewModelScope.launch {
            viewModel.setSeed(newSeed)
        }
    }

    private fun savedSeed(newSeed: Int) {
        savedStateHandle["seed"] = newSeed
        viewModelScope.launch {
            viewModel.setSeed(newSeed)
        }
    }
}