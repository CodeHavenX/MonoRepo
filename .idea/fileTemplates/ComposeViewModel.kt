package ${PACKAGE_NAME}

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ${NAME}ViewModel @Inject constructor(
) : ViewModel() {
    private val _uiState = MutableStateFlow(${NAME}UIState())
    val uiState: StateFlow<${NAME}UIState> = _uiState

    private val _event = MutableStateFlow<${NAME}Event>(${NAME}Event.Noop)
    val event: StateFlow<${NAME}Event> = _event
}
