package com.cramsan.framework.core.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.cramsan.framework.core.runSuspendCatching
import kotlinx.coroutines.launch

/**
 * Collects events from the ViewModel and executes the provided [onEvent] function for each event.
 *
 * @param viewModel The ViewModel to collect events from.
 * @param onEvent The suspend function to execute for each event.
 */
@Composable
fun <E : ViewModelEvent, UI : ViewModelUIState> rememberEventCollection(
    viewModel: BaseViewModel<E, UI>,
    onEvent: suspend (E) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            launch {
                runSuspendCatching("rememberEventCollection") {
                    onEvent(event)
                }
            }.join()
        }
    }
}
