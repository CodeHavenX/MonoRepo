package com.cramsan.framework.core

import kotlin.jvm.JvmInline

/**
 * A secure string wrapper that provides a way to handle sensitive strings safely.
 */
@SecureStringAccess
@JvmInline
value class SecureString(private val content: String) {

    constructor() : this("")

    /**
     * Returns the content of the SecureString.
     * This method is intended to be used with caution, as it exposes the sensitive content.
     */

    fun reveal(): String = content

    override fun toString(): String = "SecureString(content=****)"
}

/**
 * Annotation to mark classes or functions that access secure strings.
 * This is used to indicate that the code may handle sensitive information and should be used with caution.
 */
@RequiresOptIn(message = "Accessing a secure string. Use with caution.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class SecureStringAccess
