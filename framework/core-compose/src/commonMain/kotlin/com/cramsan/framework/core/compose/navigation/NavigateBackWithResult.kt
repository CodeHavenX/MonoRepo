package com.cramsan.framework.core.compose.navigation

import androidx.navigation.NavController
import com.cramsan.framework.core.compose.WindowEvent

/**
 * A [WindowEvent] that navigates back and deposits a typed result into the previous
 * back-stack entry's [androidx.lifecycle.SavedStateHandle], where it can be consumed via
 * [com.cramsan.framework.core.compose.ui.ObserveNavResult].
 *
 * Construct via [NavResultKey.navigateBackWith] — the internal constructor prevents
 * direct construction from outside the framework, ensuring [resultKey] and [resultValue]
 * always agree on type.
 */
data class NavigateBackWithResult internal constructor(
    val resultKey: String,
    val resultValue: Any,
) : WindowEvent

/**
 * Creates a [NavigateBackWithResult] event for this key and [value].
 *
 * Emit the returned event from a ViewModel to navigate back and deposit the result into the
 * previous back-stack entry's [androidx.lifecycle.SavedStateHandle]:
 *
 * ```kotlin
 * emitWindowEvent(MyDestination.result.navigateBackWith(selectedId))
 * ```
 */
fun <T : Any> NavResultKey<T>.navigateBackWith(value: T): NavigateBackWithResult =
    NavigateBackWithResult(resultKey = name, resultValue = value)

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
