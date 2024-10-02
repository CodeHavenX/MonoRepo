package com.cramsan.edifikana.client.lib.features.application

import com.cramsan.edifikana.client.lib.features.main.EdifikanaMainScreenEventHandler
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.framework.logging.logE

/**
 * Main screen event handler for JVM.
 */
class EdifikanaJvmMainScreenEventHandler : EdifikanaMainScreenEventHandler {

    override fun openCamera(event: MainActivityEvent.OpenCamera) {
        logE(TAG, "Opening camera is not supported on JVM")
    }

    override fun openImageExternally(event: MainActivityEvent.OpenImageExternally) {
        logE(TAG, "Opening image externally is not supported on JVM")
    }

    override fun openPhotoPicker(event: MainActivityEvent.OpenPhotoPicker) {
        logE(TAG, "Opening photo picker is not supported on JVM")
    }

    override fun shareContent(event: MainActivityEvent.ShareContent) {
        logE(TAG, "Sharing content is not supported on JVM")
    }

    override fun showSnackbar(event: MainActivityEvent.ShowSnackbar) {
        logE(TAG, "Showing snackbar is not supported on JVM")
    }

    companion object {
        private const val TAG = "EdifikanaJvmMainScreenEventHandler"
    }
}
