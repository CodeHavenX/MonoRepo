package ${PACKAGE_NAME}

import kotlin.random.Random

sealed class ${NAME}Event {
    data object Noop : ${NAME}Event()

    data class TriggerMainActivityEvent(
        val mainActivityEvent: MainActivityEvent,
        val id: Int = Random.nextInt(),
    ) : ${NAME}Event()
}
