package com.cramsan.templatereplaceme.client.lib.app

import com.cramsan.framework.logging.logE
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowsEvent

/**
 * Main screen event handler for Wasm.
 */
class TemplateReplaceMeWasmMainScreenEventHandler : TemplateReplaceMeApplicationMainScreenEventHandler {
    override fun shareContent(event: TemplateReplaceMeWindowsEvent.ShareContent) {
        logE(TAG, "Sharing content is not supported on Wasm")
    }

    companion object {
        private const val TAG = "TemplateReplaceMeWasmMainScreenEventHandler"
    }
}
