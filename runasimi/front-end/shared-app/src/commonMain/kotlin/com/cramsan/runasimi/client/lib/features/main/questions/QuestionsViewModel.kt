package com.cramsan.runasimi.client.lib.features.main.questions

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.runasimi.client.lib.manager.QuestionsManager
import kotlinx.coroutines.launch

/**
 * ViewModel for the Questions screen.
 **/
class QuestionsViewModel(dependencies: ViewModelDependencies, private val questionsManager: QuestionsManager) :
    BaseViewModel<QuestionsEvent, QuestionsUIState>(
        dependencies,
        QuestionsUIState.Initial,
        TAG,
    ) {

    /**
     * Update the UI with a new question content.
     */
    fun generateNewQuestion() {
        viewModelScope.launch {
            val content = questionsManager.generateQuestion()
            updateUiState {
                it.copy(
                    content = content,
                )
            }
        }
    }

    companion object {
        private const val TAG = "QuestionsViewModel"
    }
}
