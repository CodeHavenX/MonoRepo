package com.cramsan.framework.core.compose.navigation

/**
 * A typed key for passing a result back to a previous navigation destination via
 * [androidx.lifecycle.SavedStateHandle].
 *
 * Define one as a companion-object member on the destination that *produces* the result,
 * so both caller and callee can reference the same key without string duplication:
 *
 * ```kotlin
 * object EmployeePickerDestination : Destination {
 *     val result = NavResultKey<EmployeeId>("employee_picker_result")
 * }
 * ```
 *
 * **Producing a result** — emit [NavigateBackWithResult] via `key.navigateBackWith(value)`
 * from the producer ViewModel.
 *
 * **Consuming a result** — call [com.cramsan.framework.core.compose.ui.ObserveNavResult] inside
 * the consumer screen composable.
 *
 * ### Android serialization note
 * On Android, [androidx.lifecycle.SavedStateHandle] persists values across process death using
 * `Bundle`. Result types must therefore be primitives, `String`, or `@Parcelize`-annotated
 * classes. On JVM and WASM targets any type is accepted.
 */
class NavResultKey<T : Any>(val name: String)
