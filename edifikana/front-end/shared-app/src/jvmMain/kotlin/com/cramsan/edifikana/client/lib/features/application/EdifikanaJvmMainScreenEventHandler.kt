package com.cramsan.edifikana.client.lib.features.application

import com.cramsan.edifikana.client.lib.features.window.EdifikanaMainScreenEventHandler
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowsEvent
import com.cramsan.framework.logging.logE

/**
 * Main screen event handler for JVM.
 */
class EdifikanaJvmMainScreenEventHandler : EdifikanaMainScreenEventHandler {

    override fun openCamera(event: EdifikanaWindowsEvent.OpenCamera) {
        logE(TAG, "Opening camera is not supported on JVM")
    }

    override fun openImageExternally(event: EdifikanaWindowsEvent.OpenImageExternally) {
        logE(TAG, "Opening image externally is not supported on JVM")
    }

    override fun openPhotoPicker(event: EdifikanaWindowsEvent.OpenPhotoPicker) {
        logE(TAG, "Opening photo picker is not supported on JVM")
    }

    override fun shareContent(event: EdifikanaWindowsEvent.ShareContent) {
        logE(TAG, "Sharing content is not supported on JVM")
    }

    companion object {
        private const val TAG = "EdifikanaJvmMainScreenEventHandler"
    }
}
