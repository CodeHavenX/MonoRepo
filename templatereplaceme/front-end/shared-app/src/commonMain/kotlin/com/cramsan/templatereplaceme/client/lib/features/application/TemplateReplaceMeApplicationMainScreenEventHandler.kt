package com.cramsan.templatereplaceme.client.lib.features.application

import com.cramsan.templatereplaceme.client.lib.features.window.TemplateReplaceMeWindowsEvent

/**
 * Events that can be triggered application-wide.
 */
interface TemplateReplaceMeApplicationMainScreenEventHandler {

    /**
     * Share content.
     */
    fun shareContent(event: TemplateReplaceMeWindowsEvent.ShareContent)
}
