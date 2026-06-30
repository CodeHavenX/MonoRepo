package com.cramsan.framework.core.compose

import com.cramsan.framework.annotations.FrontendViewModel
import com.cramsan.framework.core.compose.navigation.NavResultKey
import com.cramsan.framework.core.compose.navigation.NavigateBackWithResult

/**
 * Base ViewModel for screens that produce a typed result when navigating back.
 *
 * The result key is derived automatically from the concrete subclass's simple name, so no
 * manual key string is required. The consumer screen retrieves the same key via the subclass's
 * companion object (by convention) and passes it to [com.cramsan.framework.core.compose.ui.ObserveNavResult].
 *
 * **Producing a result:**
 * ```kotlin
 * class PickColorViewModel(...) : BaseResultViewModel<Nothing, PickColorUIState, Color>(...) {
 *     fun onColorPicked(color: Color) {
 *         viewModelCoroutineScope.launch { emitWindowEvent(navigateBackFrom(color)) }
 *     }
 *     companion object {
 *         val resultKey = NavResultKey<Color>(PickColorViewModel::class.simpleName!!)
 *     }
 * }
 * ```
 *
 * **Consuming a result:**
 * ```kotlin
 * ObserveNavResult(PickColorViewModel.resultKey) { color -> viewModel.onColorReceived(color) }
 * ```
 */
@FrontendViewModel
abstract class BaseResultViewModel<E : ViewModelEvent, UI : ViewModelUIState, T : Any>(
    dependencies: ViewModelDependencies,
    initialState: UI,
    tag: String,
) : BaseViewModel<E, UI>(dependencies, initialState, tag) {

    val resultKey: NavResultKey<T> = NavResultKey(this::class.simpleName!!)

    protected fun navigateBackFrom(value: T): NavigateBackWithResult =
        NavigateBackWithResult(resultKey = resultKey.name, resultValue = value)
}
