package ${PACKAGE_NAME}

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.launch

/**
 * ViewModel for the ${NAME} screen.
 **/
 // TODO: Register this ViewModel for dependency injection
 // Look for where the ViewModel is configured and add this line
 // viewModelOf(::${NAME}ViewModel)
class ${NAME}ViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<${NAME}Event, ${NAME}UIState>(
    dependencies,
    ${NAME}UIState.Initial,
    TAG,
) {

    /**
     * Trigger the back event.
     */
    fun onBackSelected() {
        viewModelScope.launch {
            // TODO: Update this with the respective ApplicationEvent type.
            emitWindowEvent(ApplicationEvent.NavigateBack)
        }
    }

    companion object {
        private const val TAG = "${NAME}ViewModel"
    }
}
