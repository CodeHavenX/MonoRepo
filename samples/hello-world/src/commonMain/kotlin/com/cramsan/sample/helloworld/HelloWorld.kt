package com.cramsan.sample.helloworld

/**
 * Generates a greeting message with platform information.
 */
fun greeting(helloWorld: HelloWorld) = "Hello World from ${helloWorld.getPlatform()}!"

/**
 * Simple Hello World multiplatform class. To be implemented on each platform target.
 *
 * @Author cramsan
 * @created 1/23/2025
 */
expect class HelloWorld {

    /**
     * Gets the current platform name.
     */
    fun getPlatform(): String
}
