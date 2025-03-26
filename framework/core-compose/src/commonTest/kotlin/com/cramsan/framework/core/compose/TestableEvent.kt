package com.cramsan.framework.core.compose

sealed class TestableEvent : ViewModelEvent {

    /**
     * No operation.
     */
    data object Noop : TestableEvent()

    /**
     * Emit a number.
     */
    data class EmitNumber(val number: Int) : TestableEvent()
}