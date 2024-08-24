package com.codehavenx.alpaca.frontend.appcore.features.application

/**
 * This annotation denotes that this value represents a path, but it is not safe to be used directly. This is needed as
 * several paths have placeholders that need to be filled to be usable.
 */
@RequiresOptIn(message = "You should not use Route.path directly, bur rather a function that returns the path.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY)
annotation class RouteUnsafePath
