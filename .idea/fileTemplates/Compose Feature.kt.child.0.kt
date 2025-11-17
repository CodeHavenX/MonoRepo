package ${PACKAGE_NAME}.${Package_Name}

import com.cramsan.framework.core.compose.ViewModelEvent
import kotlin.random.Random

/**
 * Events that can be triggered within the domain of the ${Feature_Name} feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class ${Feature_Name}Event : ViewModelEvent {
    /**
     * No operation.
     */
    data object Noop : ${Feature_Name}Event()
}