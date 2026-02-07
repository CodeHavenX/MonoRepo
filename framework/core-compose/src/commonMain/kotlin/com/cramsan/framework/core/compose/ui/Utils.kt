package com.cramsan.framework.core.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.ViewModelEvent
import com.cramsan.framework.core.compose.ViewModelUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Composable to observe ViewModel events. When an event is emitted, the provided [block] is executed.
 *
 * This function uses a [rememberCoroutineScope] to launch the [block] in a coroutine.
 * This is needed as exceptions in the [block] would otherwise cancel the [LaunchedEffect].
 */
@Composable
fun <E : ViewModelEvent, UI : ViewModelUIState>ObserveViewModelEvents(
    viewModel: BaseViewModel<E, UI>,
    block: suspend CoroutineScope.(E) -> Unit,
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            scope.launch {
                block(event)
            }
        }
    }
}

/**
 * Composable to observe [EventEmitter] events. When an event is emitted, the provided [block] is executed.
 *
 * This function uses a [rememberCoroutineScope] to launch the [block] in a coroutine.
 * This is needed as exceptions in the [block] would otherwise cancel the [LaunchedEffect].
 */
@Composable
fun <E>ObserveEventEmitterEvents(
    eventEmitter: EventEmitter<E>,
    block: suspend CoroutineScope.(E) -> Unit,
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(eventEmitter) {
        eventEmitter.events.collect { event ->
            scope.launch {
                block(event)
            }
        }
    }
}
