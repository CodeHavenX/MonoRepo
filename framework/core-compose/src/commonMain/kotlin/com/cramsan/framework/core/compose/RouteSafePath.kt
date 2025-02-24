package com.cramsan.framework.core.compose

/**
 * Safe path for a route.
 */
@RequiresOptIn(message = "You should not use Route.path directly, bur rather a function that returns the path.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY)
annotation class RouteSafePath
