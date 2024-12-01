package ${PACKAGE_NAME}

import kotlin.random.Random

/**
 * Events that can be triggered within the domain of the ${NAME} feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class ${NAME}Event {

    /**
     * No operation.
     */
    data object Noop : ${NAME}Event()

    /**
     * Trigger application event. This event is sent to the application's view model to be handled.
     */
    data class TriggerApplicationEvent(
        // Update this with the respective ApplicationEvent type.
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : ${NAME}Event()
    
    /**
     * Trigger activity event. This event is triggered within a feature ViewModel and it will be
     * consumed by the activity that is hosting this feature.
     *
     * This event is optional, it can be removed if not needed.
     */
    data class TriggerActivityEvent(
        // Update this with the respective ActivityEvent type.
        val activityEvent: ActivityEvent,
        val id: Int = Random.nextInt(),
    ) : ${NAME}Event()
}