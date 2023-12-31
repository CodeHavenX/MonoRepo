package com.cramsan.sample.mpplib

/**
 *
 */
fun greeting(lib: MPPLib) = "Hello from MPPLib. Current Platform: ${lib.getTarget()}"

/**
 * Simple MPP class. To be implemented on each platform target.
 *
 * @Author cramsan
 * @created 1/17/2021
 */
expect class MPPLib {

    /**
     * Simple MPP function. To be implemented on each platform target.
     */
    fun getTarget(): String
}
