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
 // TODO: Register this ViewModel for dependency injection
class ${NAME}ViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel<${NAME}Event, ${NAME}UIState>(
    dependencies,
    ${NAME}UIState.Initial,
    TAG,
) {
    companion object {
        private const val TAG = "${NAME}ViewModel"
    }
}
