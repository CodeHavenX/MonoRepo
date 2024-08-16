package com.codehavenx.alpaca.shared.api.annotations

@RequiresOptIn(message = "This API uses classes that should be only used when interfacing with the network.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class ApiNetwork
