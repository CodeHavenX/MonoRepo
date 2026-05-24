package com.cramsan.framework.sample.shared.features.main.crashhandler

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the CrashHandler feature.
 */
sealed class CrashHandlerEvent : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : CrashHandlerEvent()
}
