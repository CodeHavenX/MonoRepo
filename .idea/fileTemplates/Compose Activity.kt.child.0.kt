package ${PACKAGE_NAME}

import com.cramsan.framework.core.compose.BaseViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * ${NAME} activity view model.
 */
 // TODO: Register this ViewModel for dependecy injection
class ${NAME}ActivityViewModel(
    dependencies: ViewModelDependencies,
) : BaseViewModel(dependencies) {

    private val _event = MutableSharedFlow<${NAME}ActivityEvent>()

    /**
     * Event flow to be observed.
     */
    val event: SharedFlow<${NAME}ActivityEvent> = _event

    /**
     * Execute ${NAME} Activity event.
     */
    fun execute${NAME}ActivityEvent(event: ${NAME}ActivityEvent) = viewModelScope.launch {
        _event.emit(event)
    }

    /**
     * Close the Admin activity.
     */
    fun closeActivity() = viewModelScope.launch {
        _event.emit(
            // Update this with the respective ApplicationEvent type.
            ${NAME}ActivityEvent.TriggerApplicationEvent(
                ApplicationEvent.CloseActivity()
            )
        )
    }
}
