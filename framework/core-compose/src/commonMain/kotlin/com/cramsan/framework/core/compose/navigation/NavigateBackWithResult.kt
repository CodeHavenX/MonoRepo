package com.cramsan.framework.core.compose.navigation

import androidx.navigation.NavController
import com.cramsan.framework.core.compose.WindowEvent

/**
 * A [WindowEvent] that navigates back and deposits a typed result into the previous
 * back-stack entry's [androidx.lifecycle.SavedStateHandle], where it can be consumed via
 * [com.cramsan.framework.core.compose.ui.ObserveNavResult].
 *
 * Construct via [com.cramsan.framework.core.compose.BaseResultViewModel.navigateBackFrom] — the
 * internal constructor prevents direct construction from outside the framework, ensuring
 * [resultKey] and [resultValue] always agree on type.
 */
data class NavigateBackWithResult internal constructor(
    val resultKey: String,
    val resultValue: Any,
) : WindowEvent

/**
 * Deposits the result from [event] into the previous back-stack entry's
 * [androidx.lifecycle.SavedStateHandle] and then pops back.
 *
 * Call this from the window screen's event handler when it receives a [NavigateBackWithResult]:
 *
 * ```kotlin
 * is NavigateBackWithResult -> navController.navigateBackWithResult(event)
 * ```
 */
fun NavController.navigateBackWithResult(event: NavigateBackWithResult) {
    previousBackStackEntry?.savedStateHandle?.set(event.resultKey, event.resultValue)
    popBackStack()
}
