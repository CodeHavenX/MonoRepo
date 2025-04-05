package com.cramsan.framework.core.compose

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

    fun emitNumbers() {
        viewModelScope.launch {
            emitEvent(TestableEvent.EmitNumber(1))
            emitEvent(TestableEvent.EmitNumber(2))
            emitEvent(TestableEvent.EmitNumber(3))
        }
    }

    fun emitApplicationEvent() {
        viewModelScope.launch {
            emitApplicationEvent(TestableApplicationEvent.Signal)
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
