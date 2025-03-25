package com.cramsan.framework.core.compose

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the Testable screen.
 **/
class TestableViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<TestableEvent, TestableUIState>(
    dependencies,
    TestableUIState.Initial,
    TAG,
) {

    fun setTitle(title: String) {
        viewModelScope.launch {
            updateUiState {
                it.copy(
                    title = title,
                )
            }
        }
    }

    fun throwError(exception: Throwable = IllegalStateException()) {
        viewModelScope.launch {
            throw exception
        }
    }

    companion object {
        private const val TAG = "TestableViewModel"
    }
}
