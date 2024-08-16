package com.codehavenx.alpaca.backend.storage

import com.cramsan.framework.utils.uuid.UUID

interface UniqueIdProvider {
    fun generateId(): String
}

class DefaultUniqueIdProvider : UniqueIdProvider {
    override fun generateId(): String {
        return UUID.random()
    }
}
