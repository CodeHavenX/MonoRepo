package com.cramsan.awslib.eventsystem

import com.cramsan.awslib.eventsystem.triggers.Trigger

/**
 * Implementation of a [Trigger] that will trigger an [Event] based the interation with an [GameEntityInterface] with id
 * [targetId]
 */
class CharacterTrigger(
    id: Int,
    eventId: Int,
    enabled: Boolean,
    val targetId: Int
) :
    Trigger(id, eventId, enabled)