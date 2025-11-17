package ${PACKAGE_NAME}.${Package_Name}

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the ${Feature_Name} screen.
 **/
 // TODO: Register this ViewModel for dependency injection
 // Look for where the ViewModel is configured and add this line
 // viewModelOf(::${Feature_Name}ViewModel)
class ${Feature_Name}ViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<${Feature_Name}Event, ${Feature_Name}UIState>(
    dependencies,
    ${Feature_Name}UIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            // TODO: Update this with the respective Window Event type.
            emitWindowEvent(WindowEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "${Feature_Name}ViewModel"
    }
}
