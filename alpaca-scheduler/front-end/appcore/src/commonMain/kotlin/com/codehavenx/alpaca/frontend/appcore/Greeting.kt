package com.codehavenx.alpaca.frontend.appcore

/**
 * A simple greeting class.
 */
class Greeting {

    /**
     * Returns a greeting.
     */
    fun greet(): String = GREETING_MESSAGE

    companion object {
        private const val GREETING_MESSAGE = "Hello, world!"
    }
}
