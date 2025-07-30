package com.cramsan.sample.helloworld

/**
 * Simple Hello World class. JVM Implementation.
 *
 * @Author cramsan
 * @created 1/23/2025
 */
actual class HelloWorld {
    /**
     * Gets the current platform name. JVM implementation.
     */
    actual fun getPlatform(): String = "JVM"
}
