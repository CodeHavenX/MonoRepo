package com.cramsan.framework.userevents.implementation

import com.cramsan.framework.userevents.UserEventsDelegate
import com.cramsan.framework.userevents.UserEventsInterface

/**
 * No operation implementation of [UserEventsInterface].
 */
class NoopUserEvents : UserEventsInterface {
    override val platformDelegate: UserEventsDelegate
        get() = TODO("Not yet implemented")

    override fun initialize() = Unit

    override fun log(tag: String, event: String) = Unit

    override fun log(
        tag: String,
        event: String,
        metadata: Map<String, String>
    ) = Unit
}
