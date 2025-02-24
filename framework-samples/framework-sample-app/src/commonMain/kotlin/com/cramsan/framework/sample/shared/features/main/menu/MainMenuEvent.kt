package com.cramsan.framework.sample.shared.features.main.menu

import com.cramsan.framework.core.compose.ViewModelEvent
import com.cramsan.framework.sample.shared.features.ApplicationEvent
import kotlin.random.Random

/**
 * MainMenu event.
 */
sealed class MainMenuEvent : ViewModelEvent {

    /**
     * Noop event.
     */
    data object Noop : MainMenuEvent()

    /**
     * Trigger application event.
     */
    data class TriggerApplicationEvent(
        val applicationEvent: ApplicationEvent,
        val id: Int = Random.nextInt(),
    ) : MainMenuEvent()
}
