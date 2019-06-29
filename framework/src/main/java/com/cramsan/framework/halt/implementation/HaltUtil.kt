package com.cramsan.framework.halt.implementation

import com.cramsan.framework.halt.HaltUtilInterface

actual class HaltUtil actual constructor(initializer: HaltUtilInitializer) :
    HaltUtilInterface {

    override fun stopThread() {
        var shouldStop = true
        while (shouldStop) {
            Thread.sleep(1000)
        }
    }

    override fun stopMainThread() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun crashApp() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}