package com.cramsan.flyerboard.client.lib.features.application

import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent
import com.cramsan.framework.logging.logE

/**
 * Main screen event handler for JVM.
 */
class FlyerBoardJvmMainScreenEventHandler : FlyerBoardApplicationMainScreenEventHandler {
    override fun shareContent(event: FlyerBoardWindowsEvent.ShareContent) {
        logE(TAG, "Sharing content is not supported on JVM")
    }

    companion object {
        private const val TAG = "FlyerBoardJvmMainScreenEventHandler"
    }
}
