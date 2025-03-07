package ${PACKAGE_NAME}

import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Events that can be triggered within the domain of the ${NAME} feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class ${NAME}Event : ViewModelEvent {

    /**
     * No operation.
     */
    data object Noop : ${NAME}Event()

    /**
     * Trigger application event. This event is sent to the application's view model to be handled.
     */
    data class TriggerApplicationEvent(
        // TODO: Update this with the respective ApplicationEvent type.
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : ${NAME}Event()
}