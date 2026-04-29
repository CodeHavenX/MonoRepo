package com.cramsan.framework.annotations

/**
 * Marks a class as a database model.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class DatabaseModel
