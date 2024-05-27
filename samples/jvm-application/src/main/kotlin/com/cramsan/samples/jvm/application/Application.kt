package com.cramsan.samples.jvm.application

import com.cramsan.sample.mpplib.MPPLib
import com.cramsan.samples.jvm.lib.JVMLib

/**
 * Simple JVM class.
 */
fun main() {
    val jvmLib = JVMLib()
    val mppLib = MPPLib()
    println("Welcome to a JVM application")
    println("Message from JVM Lib: ${jvmLib.getTarget()}")
    println("Message from MPP Lib: ${mppLib.getTarget()}")
}
