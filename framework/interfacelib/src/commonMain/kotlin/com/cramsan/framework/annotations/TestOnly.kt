package com.cramsan.framework.annotations

/**
 * Annotation to mark APIs that are intended for testing use only.
 *
 * This annotation is used to indicate that the annotated API is not intended for production use
 * and should only be used in testing scenarios. It serves as a warning to developers that the
 * API may not be stable or suitable for general use.
 *
 * If you need to use this API in production code, please consider refactoring the class or function
 * to a more appropriate location or creating a stable version of the API.
 */
@RequiresOptIn(message = "This API is intended for testing use only.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class TestOnly
