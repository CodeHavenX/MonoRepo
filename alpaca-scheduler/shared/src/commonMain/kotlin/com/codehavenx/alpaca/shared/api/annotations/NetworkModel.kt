package com.codehavenx.alpaca.shared.api.annotations

/**
 * Marks a class as a network model.
 */
@RequiresOptIn(message = "This API uses classes that should be only used when interfacing with the network.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class NetworkModel
