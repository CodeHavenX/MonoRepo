package ${PACKAGE_NAME}

import kotlin.random.Random

/**
 * Events that can be triggered in the ${NAME} activity.
 */
sealed class ${NAME}ActivityEvent {

    /**
     * No operation.
     */
    data object Noop : ${NAME}ActivityEvent()

    /**
     * Navigate to a destination within this activity.
     */
    data class Navigate(
        val destination: ${NAME}RouteDestination,
        val id: Int = Random.nextInt(),
    ) : ${NAME}ActivityEvent()

    /**
     * Close the ${NAME} activity.
     */
    data class CloseActivity(
        val id: Int = Random.nextInt(),
    ) : ${NAME}ActivityEvent()

    /**
     * Trigger application event.
     */
    data class TriggerApplicationEvent(
        // Update this with the respective ApplicationEvent type.
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : ${NAME}ActivityEvent()
}
