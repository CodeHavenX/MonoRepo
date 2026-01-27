package com.cramsan.runasimi.client.lib.features.main.verbs

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.runasimi.client.lib.manager.QuechuaManager
import kotlinx.coroutines.launch

/**
 * ViewModel for the Verbs screen.
 **/
class VerbsViewModel(dependencies: ViewModelDependencies, private val quechuaManager: QuechuaManager) :
    BaseViewModel<VerbsEvent, VerbsUIState>(
        dependencies,
        VerbsUIState.Initial,
        TAG,
    ) {

    /**
     * Update the UI with a new verb conjugation.
     */
    fun generateNewConjugation() {
        viewModelScope.launch {
            val content = quechuaManager.generateVerbConjugation()
            updateUiState {
                it.copy(
                    content = content,
                )
            }
        }
    }

    companion object {
        private const val TAG = "VerbsViewModel"
    }
}
