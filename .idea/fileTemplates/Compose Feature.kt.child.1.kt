package ${PACKAGE_NAME}

import androidx.lifecycle.ViewModel
import com.codehavenx.alpaca.frontend.appcore.managers.WorkContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class ${NAME}ViewModel(
    private val workContext: WorkContext,
) : ViewModel() {
    private val _uiState = MutableStateFlow(${NAME}UIState(
        content = ${NAME}UIModel(""),
        isLoading = false,
    ))
    val uiState: StateFlow<${NAME}UIState> = _uiState

    private val _event = MutableSharedFlow<${NAME}Event>()
    val event: SharedFlow<${NAME}Event> = _event
}
