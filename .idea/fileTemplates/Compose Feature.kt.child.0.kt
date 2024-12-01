package ${PACKAGE_NAME}

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for the ${NAME} screen.
 **/
class ${NAME}ViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _uiState = MutableStateFlow(${NAME}UIState(
        content = ${NAME}UIModel(""),
        isLoading = false,
    ))
    
    /**
     * UI state of the screen.
     */
    val uiState: StateFlow<${NAME}UIState> = _uiState

    private val _event = MutableSharedFlow<${NAME}Event>()

    /**
     * Event flow to be observed.
     */
    val event: SharedFlow<${NAME}Event> = _event
    
    companion object {
        private const val TAG = "${NAME}ViewModel"
    }
}
