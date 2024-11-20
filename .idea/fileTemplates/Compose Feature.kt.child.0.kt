package ${PACKAGE_NAME}

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationEvent
import kotlin.random.Random

sealed class ${NAME}Event {
    data object Noop : ${NAME}Event()

    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : ${NAME}Event()
}
