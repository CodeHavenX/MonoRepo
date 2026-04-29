package com.cramsan.framework.annotations

/**
 * Marks a class as a network model.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class NetworkModel
