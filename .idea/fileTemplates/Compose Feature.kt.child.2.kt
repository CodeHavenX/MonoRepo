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
}