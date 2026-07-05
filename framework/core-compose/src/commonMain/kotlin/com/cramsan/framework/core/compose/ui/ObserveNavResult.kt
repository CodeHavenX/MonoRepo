package com.cramsan.framework.core.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import com.cramsan.framework.core.compose.navigation.NavResultKey

/**
 * Observes a navigation result written to this destination's [androidx.lifecycle.SavedStateHandle]
 * by a screen that navigated back to here.
 *
 * When a non-null value for [key] is found, [onResult] is invoked once and the entry is
 * immediately removed so that the result is never replayed on subsequent recompositions or
 * back-navigations.
 *
 * Intended to be called near the top of a screen composable, parallel to [ObserveViewModelEvents]:
 *
 * ```kotlin
 * @Composable
 * fun SelectEmployeeCallerScreen(viewModel: CallerViewModel = koinViewModel()) {
 *     ObserveNavResult(EmployeePickerDestination.result) { employeeId ->
 *         viewModel.onEmployeePicked(employeeId)
 *     }
 *     // … rest of screen
 * }
 * ```
 *
 * @param key The [NavResultKey] that identifies the result in the back-stack entry's saved state.
 * @param onResult Called exactly once when a result arrives.
 */
@Composable
fun <T : Any> ObserveNavResult(
    key: NavResultKey<T>,
    onResult: (T) -> Unit,
) {
    // Within a NavHost composable block, LocalViewModelStoreOwner is provided as the
    // NavBackStackEntry itself (see NavBackStackEntryProvider.LocalOwnersProvider).
    // LocalNavBackStackEntry does not exist in the KMP navigation library, so this cast
    // is the supported way to reach the entry's SavedStateHandle on all targets.
    val savedStateHandle =
        (LocalViewModelStoreOwner.current as? NavBackStackEntry)?.savedStateHandle ?: return

    val result by savedStateHandle
        .getStateFlow<T?>(key.name, null)
        .collectAsState()

    LaunchedEffect(result) {
        result?.let {
            savedStateHandle.remove<T>(key.name)
            onResult(it)
        }
    }
}
