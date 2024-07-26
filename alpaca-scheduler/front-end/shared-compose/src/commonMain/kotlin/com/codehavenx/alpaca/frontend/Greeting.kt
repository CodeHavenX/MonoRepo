package com.codehavenx.alpaca.frontend

import com.codehavenx.alpaca.shared.TestShared

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name} + ${TestShared.TEST}!"
    }
}
