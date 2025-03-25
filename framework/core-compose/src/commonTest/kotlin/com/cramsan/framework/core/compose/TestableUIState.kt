package com.cramsan.framework.core.compose

data class TestableUIState(
    val title: String?,
) : ViewModelUIState {
    companion object {
        val Initial = TestableUIState(
            title = null,
        )
    }
}
