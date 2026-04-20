package com.cramsan.flyerboard.client.lib.features.application

import com.cramsan.framework.logging.logE
import com.cramsan.flyerboard.client.lib.features.window.FlyerBoardWindowsEvent

/**
 * Main screen event handler for JVM.
 */
class FlyerBoardWasmMainScreenEventHandler : FlyerBoardApplicationMainScreenEventHandler {

    override fun shareContent(event: FlyerBoardWindowsEvent.ShareContent) {
        logE(TAG, "Sharing content is not supported on Wasm")
    }

    companion object {
        private const val TAG = "FlyerBoardWasmMainScreenEventHandler"
    }
}
