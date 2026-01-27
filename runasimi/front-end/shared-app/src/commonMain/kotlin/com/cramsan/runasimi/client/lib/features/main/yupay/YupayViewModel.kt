package com.cramsan.runasimi.client.lib.features.main.yupay

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.runasimi.client.lib.manager.QuechuaManager
import kotlinx.coroutines.launch

/**
 * ViewModel for the Yupay screen.
 **/
class YupayViewModel(dependencies: ViewModelDependencies, private val quechuaManager: QuechuaManager) :
    BaseViewModel<YupayEvent, YupayUIState>(
        dependencies,
        YupayUIState.Initial,
        TAG,
    ) {

    /**
     * Update the UI with a new number.
     */
    @Suppress("MagicNumber")
    fun generateNewNumber() {
        viewModelScope.launch {
            val numberOfDigits = (1..4).random()
            val content = quechuaManager.generateNumberTranslation(numberOfDigits)
            updateUiState {
                it.copy(
                    content = content,
                )
            }
        }
    }

    companion object {
        private const val TAG = "YupayViewModel"
    }
}
