package com.cramsan.templatereplaceme.client.lib.features.application

import com.cramsan.framework.logging.logE
import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowsEvent

/**
 * Main screen event handler for JVM.
 */
class TemplateReplaceMeJvmMainScreenEventHandler : TemplateReplaceMeApplicationMainScreenEventHandler {

    override fun shareContent(event: TemplateReplaceMeWindowsEvent.ShareContent) {
        logE(TAG, "Sharing content is not supported on JVM")
    }

    companion object {
        private const val TAG = "TemplateReplaceMeJvmMainScreenEventHandler"
    }
}
