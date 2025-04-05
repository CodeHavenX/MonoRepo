package com.cramsan.framework.core.compose

sealed class TestableEvent : ViewModelEvent {

    /**
     * Emit a number.
     */
    data class EmitNumber(val number: Int) : TestableEvent()
}

sealed class TestableApplicationEvent : ApplicationViewModelEvent {
    /**
     * Emit a number.
     */
    data object Signal : TestableApplicationEvent()
}