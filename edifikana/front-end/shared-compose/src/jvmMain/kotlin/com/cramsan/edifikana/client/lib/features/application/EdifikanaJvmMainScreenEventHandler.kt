package com.cramsan.edifikana.client.lib.features.application

import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationEvent
import com.cramsan.edifikana.client.lib.features.root.EdifikanaMainScreenEventHandler
import com.cramsan.framework.logging.logE

/**
 * Main screen event handler for JVM.
 */
class EdifikanaJvmMainScreenEventHandler : EdifikanaMainScreenEventHandler {

    override fun openCamera(event: EdifikanaApplicationEvent.OpenCamera) {
        logE(TAG, "Opening camera is not supported on JVM")
    }

    override fun openImageExternally(event: EdifikanaApplicationEvent.OpenImageExternally) {
        logE(TAG, "Opening image externally is not supported on JVM")
    }

    override fun openPhotoPicker(event: EdifikanaApplicationEvent.OpenPhotoPicker) {
        logE(TAG, "Opening photo picker is not supported on JVM")
    }

    override fun shareContent(event: EdifikanaApplicationEvent.ShareContent) {
        logE(TAG, "Sharing content is not supported on JVM")
    }

    companion object {
        private const val TAG = "EdifikanaJvmMainScreenEventHandler"
    }
}
